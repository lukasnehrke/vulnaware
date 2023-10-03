package dev.lukasnehrke.vulnaware.bom.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.packageurl.MalformedPackageURLException;
import com.github.packageurl.PackageURL;
import dev.lukasnehrke.vulnaware.bom.model.Package;
import dev.lukasnehrke.vulnaware.bom.model.PackageLink;
import dev.lukasnehrke.vulnaware.bom.repository.BomRepository;
import dev.lukasnehrke.vulnaware.bom.repository.PackageRepository;
import dev.lukasnehrke.vulnaware.util.schema.osi.QueryResponse;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class LookupService {

    private static final String REQ = "https://api.deps.dev/v3alpha/systems/%s/packages/%s";
    private static final String REQ_VERSION = "https://api.deps.dev/v3alpha/systems/%s/packages/%s/versions/%s";
    private static final Logger logger = LoggerFactory.getLogger(LookupService.class);
    private final BomRepository boms;
    private final PackageRepository packages;

    @Transactional
    public void lookupLatest() {
        /* TODO: update latest versions */
    }

    @Async
    @Transactional
    public void lookupBom(final Long id) {
        final var bom = boms.findById(id).orElseThrow();

        int updates = 0;
        for (final var c : bom.getComponents()) {
            final var pkg = c.getPkg();
            if (pkg == null) continue;
            if (pkg.getLastLookupAt() != null) {
                if (pkg.getLatestVersion() != null) {
                    updates++;
                }
                continue;
            }
            if (queryPackage(pkg)) {
                updates++;
            }
        }

        final var metrics = bom.getMetrics();
        metrics.setUpdatesCount(updates);
        bom.setMetrics(metrics);
        boms.save(bom);
    }

    private boolean queryPackage(final Package pkg) {
        final var now = Instant.now();
        boolean hasUpdate = false;

        try {
            final var url = getRequest(pkg, true);
            if (url != null) {
                final var res = new RestTemplate().getForEntity(url, QueryResponse.class);

                final var body = res.getBody();
                if (body == null) return false;

                pkg.setInvalid(false);
                pkg.setLicenses(new ArrayList<>(body.licenses()));
                pkg.setLinks(body.links().stream().map(link -> new PackageLink(link.label(), link.url())).collect(Collectors.toList()));

                if (!body.isDefault()) {
                    hasUpdate = queryVersions(pkg);
                }
            }
        } catch (MalformedPackageURLException ex) {
            logger.warn("Failed to parse package url: {}", pkg.getId(), ex);
        } catch (HttpClientErrorException ex) {
            pkg.setInvalid(true);
            logger.warn("Failed to fetch license version information for '{}': {}", pkg.getId(), ex.getStatusCode());
        }

        pkg.setLastLookupAt(Date.from(now));
        packages.save(pkg);
        return hasUpdate;
    }

    private boolean queryVersions(final Package pkg) {
        try {
            final var purl = new PackageURL(pkg.getId());
            final var url = getRequest(pkg, false);
            if (url != null) {
                final var res = new RestTemplate().getForEntity(url, JsonNode.class);

                final var body = res.getBody();
                if (body == null) return false;

                boolean isBefore = false;
                for (final var el : res.getBody().get("versions")) {
                    final var version = el.get("versionKey").get("version").asText();
                    final var isDef = el.hasNonNull("isDefault") && el.get("isDefault").asBoolean();

                    if (isDef && isBefore) {
                        pkg.setLatestVersion(version);
                        return true;
                    }

                    if (purl.getVersion().equals(version)) {
                        isBefore = true;
                    }
                }
            }
        } catch (MalformedPackageURLException ex) {
            logger.warn("Failed to parse package url: {}", pkg.getId(), ex);
        } catch (HttpClientErrorException ex) {
            logger.warn("Failed to fetch latest version for '{}': {}", pkg.getId(), ex.getStatusCode());
        }

        return false;
    }

    @Nullable
    private static URI getRequest(Package pkg, boolean withVersion) throws MalformedPackageURLException {
        final var purl = new PackageURL(pkg.getId());

        String ecosystem, name, version;
        switch (purl.getType()) {
            case "deb" -> {
                return null;
            }
            case "maven" -> {
                ecosystem = "maven";
                name = purl.getNamespace() + ":" + purl.getName();
                version = purl.getVersion();
            }
            default -> {
                ecosystem = purl.getType();
                if (purl.getNamespace() == null) {
                    name = purl.getName();
                } else {
                    name = purl.getNamespace() + "%2F" + purl.getName();
                }
                version = purl.getVersion();
            }
        }

        if (withVersion) {
            return URI.create(String.format(REQ_VERSION, ecosystem, name, version));
        }
        return URI.create(String.format(REQ, ecosystem, name));
    }
    /*
    @Nullable
    private String fetchLatestVersion(final Component component) {
        final String ecosystem = convertEcosystem(component.getEcosystem());
        if (ecosystem == null || component.getVersion() == null) return null;

        try {
            final var url = String.format(REQ, ecosystem, component.getName());
            final var res = new RestTemplate().getForEntity(url, JsonNode.class);

            if (!res.getStatusCode().is2xxSuccessful() || res.getBody() == null) {
                logger.warn("Failed to fetch version information for '{}': {}", component.getName(), res.getStatusCode());
                return null;
            }

            for (final var el : res.getBody().get("versions")) {
                final var version = el.get("versionKey").get("version").asText();
                final var isDef = el.hasNonNull("isDefault") && el.get("isDefault").asBoolean();

                if (isDef && !component.getVersion().equals(version)) {
                    return version;
                }
            }
        } catch (HttpClientErrorException ex) {
            logger.warn("Failed to fetch version information for '{}': {}", component.getName(), ex.getStatusCode());
        }
        return null;
    }

    @Nullable
    private List<String> fetchLicense(final Component component) {
        final String ecosystem = convertEcosystem(component.getEcosystem());
        if (ecosystem == null) return null;

        try {
            final var url = String.format(REQ_VERSION, ecosystem, component.getName(), component.getVersion());
            final var res = new RestTemplate().getForEntity(url, JsonNode.class);

            if (!res.getStatusCode().is2xxSuccessful() || res.getBody() == null) {
                logger.warn("Failed to license version information for '{}': {}", component.getName(), res.getStatusCode());
                return null;
            }

            final var body = res.getBody();

            if (body.hasNonNull("licenses")) {
                final List<String> list = new ArrayList<>();
                for (final var el : body.get("licenses")) {
                    list.add(el.asText());
                }
                return list;
            }
        } catch (HttpClientErrorException ex) {
            logger.warn("Failed to license version information for '{}': {}", component.getName(), ex.getStatusCode());
        }
        return null;
    }

    @Nullable
    private String convertEcosystem(final PackageURL url) {
        return switch (ecosystem) {
            case GO, NPM, MAVEN -> ecosystem.name().toLowerCase();
            default -> null;
        };
    }
    */
}
