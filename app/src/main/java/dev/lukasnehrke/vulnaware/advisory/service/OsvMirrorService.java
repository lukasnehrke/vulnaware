package dev.lukasnehrke.vulnaware.advisory.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import dev.lukasnehrke.vulnaware.advisory.model.AffectedPackage;
import dev.lukasnehrke.vulnaware.advisory.model.DataSource;
import dev.lukasnehrke.vulnaware.advisory.model.OsvAdvisory;
import dev.lukasnehrke.vulnaware.advisory.repository.DataSourceRepository;
import dev.lukasnehrke.vulnaware.advisory.repository.OsvAdvisoryRepository;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.zip.ZipInputStream;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;
import us.springett.cvss.Cvss;

@Service
@RequiredArgsConstructor
public class OsvMirrorService {

    private static final Logger logger = LoggerFactory.getLogger(OsvMirrorService.class);
    private final Storage storage = StorageOptions.newBuilder().build().getService();
    private final ObjectMapper mapper = new ObjectMapper();
    private final DataSourceRepository dataSources;
    private final OsvAdvisoryRepository advisories;

    @Value("${vulnaware.osv.ecosystems}")
    private String[] ecosystems;

    @Transactional(propagation = Propagation.REQUIRED)
    public void syncEcosystem(final String ecosystem) throws IOException {
        final StopWatch watch = new StopWatch();

        final var blob = storage.get(BlobId.of("osv-vulnerabilities", ecosystem + "/all.zip"));
        if (blob == null) {
            logger.warn("Ecosystem '{}' does not exist in the OSV storage bucket", ecosystem);
            return;
        }

        final var source = dataSources.getDataSourceById("osv-" + ecosystem);
        if (source.isPresent() && source.get().getEtag() != null) {
            // check if the zip etag has changed since the last synchronization
            if (blob.getEtag().equals(source.get().getEtag())) {
                logger.info("Ecosystem '{}' is up-to-date ({})", ecosystem, blob.getUpdateTimeOffsetDateTime());
                return;
            }
        }

        watch.start("zip download");
        final var zip = Files.createTempDirectory("va-osv-cache").resolve(ecosystem + ".zip");
        blob.downloadTo(zip);
        watch.stop();

        logger.info("Downloaded 'osv-vulnerabilities/{}/all.zip' to '{}' ({}s)", ecosystem, zip, watch.getTotalTimeSeconds());

        try (final var zis = new ZipInputStream(new FileInputStream(zip.toFile()))) {
            var zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                final var json = mapper.readTree(zis.readAllBytes());

                if (!json.hasNonNull("id") || !json.hasNonNull("modified")) {
                    logger.warn("Skipping invalid OSV entry {}", json);
                    zipEntry = zis.getNextEntry();
                    continue;
                }

                if (source.isPresent() && source.get().getLastSynchronized() != null) {
                    // check if the entry has been updated since the last synchronization
                    if (!OffsetDateTime.parse(json.get("modified").asText()).isAfter(source.get().getLastSynchronized())) {
                        zipEntry = zis.getNextEntry();
                        continue;
                    }
                }

                final var affectedSet = new HashSet<AffectedPackage>();
                for (final var affected : json.get("affected")) {
                    final var pkg = new AffectedPackage();
                    pkg.setOsvEcosystem(transformEcosystem(affected.get("package").get("ecosystem").asText()));
                    pkg.setOsvName(affected.get("package").get("name").asText());
                    affectedSet.add(pkg);
                }

                final var advisory = new OsvAdvisory();
                advisory.setId(json.get("id").asText());
                advisory.setAffectedPackages(affectedSet);

                // get risk from cvss score
                advisory.setRisk(extractRiskScore(json));

                // note: withdrawn is a timestamp
                advisory.setWithdrawn(json.hasNonNull("withdrawn"));

                if (json.hasNonNull("summary")) {
                    advisory.setSummary(json.get("summary").asText());
                    ((ObjectNode) json).remove("summary"); // don't store summary twice
                }

                // store osv data as json string
                advisory.setOsvData(json.toString());

                advisories.save(advisory);
                zipEntry = zis.getNextEntry();
            }
        }

        if (source.isPresent()) {
            source.get().setEtag(blob.getEtag());
            source.get().setLastSynchronized(blob.getUpdateTimeOffsetDateTime());
        } else {
            final var newSource = new DataSource();
            newSource.setId("osv-" + ecosystem);
            newSource.setEtag(blob.getEtag());
            newSource.setLastSynchronized(blob.getUpdateTimeOffsetDateTime());
            dataSources.save(newSource);
        }

        logger.info("Finished synchronization of ecosystem '{}'", ecosystem);
    }

    @Nullable
    private Double extractRiskScore(final JsonNode osv) {
        Double score = null;

        if (osv.hasNonNull("severity")) {
            for (final var severity : osv.get("severity")) {
                if (severity.get("type").asText().equals("CVSS_V3")) {
                    final var cvss = Cvss.fromVector(severity.get("score").asText());
                    if (cvss == null) continue;
                    return cvss.calculateScore().getBaseScore();
                }
                if (severity.get("type").asText().equals("CVSS_V2")) {
                    final var cvss = Cvss.fromVector(severity.get("score").asText());
                    if (cvss == null) continue;
                    score = cvss.calculateScore().getBaseScore();
                }
            }
        }

        return score;
    }

    private String transformEcosystem(String ecosystem) {
        if (ecosystem.startsWith("Debian")) return "Debian";
        return ecosystem;
    }

    public String[] getEcosystems() {
        return ecosystems;
    }
}
