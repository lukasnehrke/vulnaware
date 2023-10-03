package dev.lukasnehrke.vulnaware.bom.service.export;

import dev.lukasnehrke.vulnaware.advisory.model.Advisory;
import dev.lukasnehrke.vulnaware.bom.model.Bom;
import dev.lukasnehrke.vulnaware.bom.model.BomFormat;
import dev.lukasnehrke.vulnaware.bom.model.Component;
import dev.lukasnehrke.vulnaware.bom.repository.BomRepository;
import dev.lukasnehrke.vulnaware.storage.model.Asset;
import dev.lukasnehrke.vulnaware.storage.service.AssetService;
import dev.lukasnehrke.vulnaware.storage.service.StorageService;
import dev.lukasnehrke.vulnaware.vulnerability.model.Vulnerability;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.cyclonedx.BomParserFactory;
import org.cyclonedx.exception.ParseException;
import org.cyclonedx.generators.json.BomJsonGenerator14;
import org.cyclonedx.model.Metadata;

@org.springframework.stereotype.Component
@RequiredArgsConstructor
public class CycloneDxExporter {

    private final StorageService storageService;

    public String export(final Bom bom, final boolean includeVulns) {
        try {
            // get bom from persistent storage (if exists)
            final org.cyclonedx.model.Bom bomDx;
            if (bom.getAsset() != null) {
                bomDx = fromAsset(bom.getAsset());
            } else {
                bomDx = new org.cyclonedx.model.Bom();
            }

            // bom metadata
            final var metadata = bomDx.getMetadata() != null ? bomDx.getMetadata() : new Metadata();
            metadata.setTimestamp(bom.getCreatedAt());
            bomDx.setMetadata(metadata);

            bomDx.setComponents(bom.getComponents().stream().map(this::toComponent).toList());
            if (includeVulns) {
                bomDx.setVulnerabilities(bom.getVulnerabilities().stream().map(this::toVulnerability).toList());
            }

            return new BomJsonGenerator14(bomDx).toJsonString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private org.cyclonedx.model.Bom fromAsset(final Asset asset) throws IOException, ParseException {
        final var resource = storageService.loadResource(asset.getLocation());
        final var bytes = resource.getContentAsByteArray();
        return BomParserFactory.createParser(bytes).parse(bytes);
    }

    private org.cyclonedx.model.Component toComponent(final Component component) {
        final var cycloneDxComponent = new org.cyclonedx.model.Component();

        cycloneDxComponent.setName(component.getName());
        cycloneDxComponent.setBomRef(getRef(component));

        // set purl (if present)
        if (component.getPackageURL() != null) {
            cycloneDxComponent.setPurl(component.getPackageURL());
        }

        // set cpe (if present)
        if (component.getCpe() != null) {
            cycloneDxComponent.setCpe(component.getCpe());
        }

        return cycloneDxComponent;
    }

    public org.cyclonedx.model.vulnerability.Vulnerability toVulnerability(final Vulnerability vuln) {
        final var vulnDx = new org.cyclonedx.model.vulnerability.Vulnerability();

        // TODO annotate osv/nvd specific fields
        vulnDx.setBomRef(vuln.getAdvisory().getId() + "-" + vuln.getId());
        vulnDx.setId(vuln.getAdvisory().getId());
        vulnDx.setSource(getSource(vuln.getAdvisory()).orElse(null));
        vulnDx.setDescription(vuln.getAdvisory().getSummary());

        vulnDx.setAffects(
            vuln
                .getComponents()
                .stream()
                .map(c -> {
                    final var affect = new org.cyclonedx.model.vulnerability.Vulnerability.Affect();
                    affect.setRef(getRef(c));
                    affect.setVersions(List.of());
                    return affect;
                })
                .toList()
        );

        return vulnDx;
    }

    private Optional<org.cyclonedx.model.vulnerability.Vulnerability.Source> getSource(final Advisory advisory) {
        if (advisory.getType().equals("OSV")) {
            final var source = new org.cyclonedx.model.vulnerability.Vulnerability.Source();
            source.setName("OSV");
            source.setUrl("https://osv.dev/vulnerability/" + advisory.getId());
            return Optional.of(source);
        }

        if (advisory.getType().equals("NVD")) {
            final var source = new org.cyclonedx.model.vulnerability.Vulnerability.Source();
            source.setName("NVD");
            source.setUrl("https://nvd.nist.gov/vuln/detail/" + advisory.getId());
            return Optional.of(source);
        }

        return Optional.empty();
    }

    private String getRef(final Component component) {
        if (component.getBom().getFormat() == BomFormat.CYCLONE_DX && component.getBomRef() != null) {
            return component.getBomRef();
        }
        return component.getName() + "-" + component.getId();
    }
}
