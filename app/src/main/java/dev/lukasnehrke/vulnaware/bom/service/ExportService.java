package dev.lukasnehrke.vulnaware.bom.service;

import dev.lukasnehrke.vulnaware.bom.model.Annotation;
import dev.lukasnehrke.vulnaware.bom.model.Bom;
import dev.lukasnehrke.vulnaware.bom.model.Component;
import dev.lukasnehrke.vulnaware.bom.repository.BomRepository;
import dev.lukasnehrke.vulnaware.bom.service.export.CycloneDxExporter;
import dev.lukasnehrke.vulnaware.bom.service.export.SpdxExporter;
import dev.lukasnehrke.vulnaware.project.model.Project;
import dev.lukasnehrke.vulnaware.storage.model.Asset;
import dev.lukasnehrke.vulnaware.storage.service.AssetService;
import dev.lukasnehrke.vulnaware.storage.service.StorageService;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExportService {

    private static final Logger logger = LoggerFactory.getLogger(ExportService.class);
    private final BomService bomService;
    private final AssetService assetService;
    private final StorageService storageService;
    private final BomRepository boms;
    private final CycloneDxExporter cycloneDxExporter;
    private final SpdxExporter spdxExporter;

    @Transactional
    public String exportToCycloneDx(final Project project, final String tag, final boolean includeVulns) throws IOException {
        final var bom = bomService.findBomOrThrow(project, tag);

        final var label = String.format("%s/annotation/cdx" + (includeVulns ? "-vulns" : ""), bom.getId());
        final var annotation = bom.getAnnotations().stream().filter(a -> a.getLabel().equals(label)).findFirst();
        if (annotation.isPresent()) {
            logger.info("Returning exported CDX BOM '{}' from cache", bom.getId());
            final var asset = annotation.get().getAsset();
            return storageService.loadResource(asset.getLocation()).getContentAsString(StandardCharsets.UTF_8);
        }

        logger.info("Exporting BOM '{} to CycloneDX'", bom.getId());
        final var result = cycloneDxExporter.export(bom, includeVulns);

        saveResult(bom, label, result);
        return result;
    }

    @Transactional
    public String exportToSpdx(final Project project, final String tag, final boolean includeVulns) throws IOException {
        final var bom = bomService.findBomOrThrow(project, tag);

        final var label = String.format("%s/annotation/spdx" + (includeVulns ? "-vulns" : ""), bom.getId());
        final var annotation = bom.getAnnotations().stream().filter(a -> a.getLabel().equals(label)).findFirst();
        if (annotation.isPresent()) {
            logger.info("Returning exported SPDX BOM '{}' from cache", bom.getId());
            final var asset = annotation.get().getAsset();
            return storageService.loadResource(asset.getLocation()).getContentAsString(StandardCharsets.UTF_8);
        }

        logger.info("Exporting BOM '{} to SPDX'", bom.getId());
        final var result = spdxExporter.export(bom, includeVulns);

        saveResult(bom, label, result);
        return result;
    }

    @Transactional(readOnly = true)
    public void exportToCsv(final Project project, final String tag, final Writer writer) throws Exception {
        final var bom = bomService.findBomOrThrow(project, tag);
        logger.debug("Begin export of SBOM '{}' to CSV", bom.getId());

        final List<Component> components = bom.getComponents();
        logger.info("Writing {} components to CSV", components.size());

        try (final CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
            printer.printRecord("Name", "PackageURL", "CPE", "Vulnerabilities");
            for (final Component component : components) {
                printer.printRecord(
                    component.getName(),
                    component.getPackageURL(),
                    component.getCpe(),
                    component.getVulnerabilities().stream().map(s -> s.getAdvisory().getId()).toList()
                );
            }
        }
    }

    private void saveResult(final Bom bom, String label, String result) throws IOException {
        // save annotated bom to persistent storage
        try (InputStream stream = new ByteArrayInputStream(result.getBytes(StandardCharsets.UTF_8))) {
            final Asset asset = assetService.createAsset(label, stream);
            final var ann = new Annotation();
            ann.setLabel(label);
            ann.setAsset(asset);
            bom.getAnnotations().add(ann);
            boms.save(bom);
        }
    }
}
