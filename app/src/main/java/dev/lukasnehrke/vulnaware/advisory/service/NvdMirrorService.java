package dev.lukasnehrke.vulnaware.advisory.service;

import com.fasterxml.jackson.databind.JsonNode;
import dev.lukasnehrke.vulnaware.advisory.model.AffectedPackage;
import dev.lukasnehrke.vulnaware.advisory.model.DataSource;
import dev.lukasnehrke.vulnaware.advisory.model.NvdAdvisory;
import dev.lukasnehrke.vulnaware.advisory.repository.DataSourceRepository;
import dev.lukasnehrke.vulnaware.advisory.repository.NvdAdvisoryRepository;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import us.springett.parsers.cpe.CpeParser;
import us.springett.parsers.cpe.exceptions.CpeParsingException;

@Service
public class NvdMirrorService {

    private static final Logger logger = LoggerFactory.getLogger(NvdMirrorService.class);
    private static final RestTemplate restTemplate = new RestTemplate();
    private final DataSourceRepository dataSources;
    private final NvdAdvisoryRepository advisories;

    NvdMirrorService(final DataSourceRepository dataSources, final NvdAdvisoryRepository advisories) {
        this.dataSources = dataSources;
        this.advisories = advisories;
    }

    @Transactional
    public boolean sync() {
        final StopWatch watch = new StopWatch();
        watch.start("sync");

        final var now = OffsetDateTime.now(ZoneId.of("UTC"));
        final var source = dataSources.getDataSourceById("nvd");

        final var url = UriComponentsBuilder.fromHttpUrl("https://services.nvd.nist.gov/rest/json/cves/2.0");

        if (source.isPresent() && source.get().getCursor() != null) {
            url.queryParam("startIndex", source.get().getCursor());
        } else {
            url.queryParam("startIndex", 0);
        }

        if (source.isPresent() && source.get().getLastSynchronized() != null) {
            url.queryParam("lastModStartDate", source.get().getLastSynchronized().toString());
            url.queryParam("lastModEndDate", now.toString());
        }

        final var restTemplate = new RestTemplate();
        final var res = restTemplate.getForEntity(url.toUriString(), JsonNode.class);

        if (!res.getStatusCode().is2xxSuccessful() || res.getBody() == null) {
            logger.error("Failed to fetch NVD data: {}", res.getStatusCode());
            return false;
        }

        final var body = res.getBody();
        final var vulns = res.getBody().get("vulnerabilities");
        logger.info("Fetched {} vulnerabilities from NVD", vulns.size());

        for (final var item : vulns) {
            if (!item.hasNonNull("cve")) continue;

            final var advisory = new NvdAdvisory();
            advisory.setId(item.get("cve").get("id").asText());
            advisory.setWithdrawn(isWithdrawn(item.get("cve")));
            advisory.setRisk(getRisk(item.get("cve")));
            advisory.setSummary(getSummary(item.get("cve")));
            advisory.setAffectedPackages(getAffectedPackages(item.get("cve")));
            advisory.setNvdData(item.toString());

            advisories.save(advisory);
        }

        final var dataSource = source.orElseGet(() -> {
            final var newSource = new DataSource();
            newSource.setId("nvd");
            return newSource;
        });

        boolean hasNextPage = false;
        if (!vulns.isEmpty() && body.get("totalResults").asInt() > body.get("resultsPerPage").asInt()) {
            final var cursor = dataSource.getCursor() == null ? 0 : dataSource.getCursor();
            dataSource.setCursor(cursor + body.get("resultsPerPage").asInt());
            dataSource.setLastSynchronized(null);
            hasNextPage = true;
        } else {
            dataSource.setCursor(null);
            dataSource.setLastSynchronized(now);
        }

        dataSources.save(dataSource);
        return hasNextPage;
    }

    private boolean isWithdrawn(final JsonNode node) {
        return node.hasNonNull("vulnStatus") && node.get("vulnStatus").asText().equals("Rejected");
    }

    @Nullable
    private String getSummary(final JsonNode node) {
        /*
        if (!node.hasNonNull("descriptions")) return null;
        for (final var item : node.get("descriptions")) {
            if (item.hasNonNull("lang") && item.get("lang").asText().equals("en")) {
                final var summary = item.get("value").asText();
                return summary.substring(0, Math.min(summary.length(), 255));
            }
        }
         */
        return null;
    }

    @Nullable
    private Double getRisk(final JsonNode node) {
        if (!node.hasNonNull("metrics")) return null;
        if (node.get("metrics").has("cvssMetricV31")) {
            return getRiskFromMetric(node.get("metrics").get("cvssMetricV31"));
        }
        if (node.get("metrics").has("cvssMetricV3")) {
            return getRiskFromMetric(node.get("metrics").get("cvssMetricV3"));
        }
        if (node.get("metrics").has("cvssMetricV2")) {
            return getRiskFromMetric(node.get("metrics").get("cvssMetricV2"));
        }
        return null;
    }

    @Nullable
    private Double getRiskFromMetric(final JsonNode metric) {
        for (final var item : metric) {
            if (item.hasNonNull("type") && item.get("type").asText().equals("Primary")) {
                return item.get("cvssData").get("baseScore").asDouble();
            }
        }
        return null;
    }

    private HashSet<AffectedPackage> getAffectedPackages(final JsonNode cve) {
        final var set = new HashSet<AffectedPackage>();
        if (!cve.hasNonNull("configurations")) return set;

        for (final var item : cve.get("configurations")) {
            if (item.hasNonNull("nodes")) {
                for (final var node : item.get("nodes")) {
                    if (!node.hasNonNull("cpeMatch")) continue;
                    for (final var cpeMatch : node.get("cpeMatch")) {
                        final var criteria = cpeMatch.get("criteria").asText();
                        try {
                            final var cpe = CpeParser.parse(criteria);

                            final var affectedPackage = new AffectedPackage();
                            affectedPackage.setCpeVendor(cpe.getVendor());
                            affectedPackage.setCpeProduct(cpe.getProduct());
                            affectedPackage.setCpeVersion(cpe.getVersion().equals("*") ? null : cpe.getVersion());
                            set.add(affectedPackage);
                        } catch (CpeParsingException e) {
                            logger.warn("Invalid CPE in NVD dataset: {}", criteria);
                        }
                    }
                }
            }
        }

        return set;
    }
}
