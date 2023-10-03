package dev.lukasnehrke.vulnaware.bom.service.export;

import static org.spdx.library.SpdxConstants.CLASS_EXTERNAL_DOC_REF;
import static org.spdx.library.SpdxConstants.CLASS_SPDX_PACKAGE;

import dev.lukasnehrke.vulnaware.advisory.model.Advisory;
import dev.lukasnehrke.vulnaware.bom.model.Bom;
import dev.lukasnehrke.vulnaware.bom.model.BomFormat;
import dev.lukasnehrke.vulnaware.bom.model.DataFormat;
import dev.lukasnehrke.vulnaware.storage.model.Asset;
import dev.lukasnehrke.vulnaware.storage.service.StorageService;
import dev.lukasnehrke.vulnaware.vulnerability.model.Vulnerability;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.cyclonedx.BomParserFactory;
import org.cyclonedx.exception.ParseException;
import org.spdx.jacksonstore.MultiFormatStore;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.ModelCopyManager;
import org.spdx.library.SpdxConstants;
import org.spdx.library.model.ReferenceType;
import org.spdx.library.model.SpdxDocument;
import org.spdx.library.model.SpdxModelFactory;
import org.spdx.library.model.SpdxPackage;
import org.spdx.library.model.enumerations.ReferenceCategory;
import org.spdx.library.model.license.AnyLicenseInfo;
import org.spdx.library.model.license.LicenseInfoFactory;
import org.spdx.storage.IModelStore;
import org.spdx.storage.ISerializableModelStore;
import org.spdx.storage.simple.InMemSpdxStore;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpdxExporter {

    private final StorageService storageService;

    public String export(final Bom bom, boolean includeVulns) {
        try {
            // fetch or create spdx document
            ModelCopyManager cm = new ModelCopyManager();
            ISerializableModelStore store;
            SpdxDocument doc;

            // create a map with bomRef and vulns
            final Map<String, List<Vulnerability>> map = new HashMap<>();

            if (bom.getFormat() == BomFormat.SPDX && bom.getAsset() != null && bom.getDataFormat() != null) {
                // fetch existing document
                store = createStore(bom.getDataFormat());
                if (store == null) throw new RuntimeException("Invalid data format: " + bom.getDataFormat());
                final var uri = store.deSerialize(storageService.loadResource(bom.getAsset().getLocation()).getInputStream(), true);
                doc = new SpdxDocument(store, uri, null, false);

                for (final var component : bom.getComponents()) {
                    map.put(component.getBomRef(), component.getVulnerabilities());
                }
            } else {
                store = new MultiFormatStore(new InMemSpdxStore(), MultiFormatStore.Format.JSON);
                doc = SpdxModelFactory.createSpdxDocument(store, "https://lukasnehrke.dev", cm);

                final var noLicense = LicenseInfoFactory.parseSPDXLicenseString("NOASSERTION");
                for (final var c : bom.getComponents()) {
                    final var id = store.getNextId(IModelStore.IdType.SpdxId, doc.getDocumentUri());
                    final var pkg = doc
                        .createPackage(id, c.getName(), noLicense, "", noLicense)
                        .setFilesAnalyzed(false)
                        .setDownloadLocation("NOASSERTION")
                        .build();

                    doc.getDocumentDescribes().add(pkg);
                    map.put(id, c.getVulnerabilities());
                }
            }

            if (includeVulns) {
                SpdxModelFactory
                    .getElements(store, doc.getDocumentUri(), null, SpdxPackage.class)
                    .map(SpdxPackage.class::cast)
                    .forEach(pkg -> {
                        final var vulns = map.get(pkg.getId());
                        if (vulns == null) return;
                        try {
                            for (final var vuln : vulns) {
                                final var locator = getSource(vuln.getAdvisory());
                                if (locator == null) continue;

                                final var el = SpdxModelFactory.createModelObject(
                                    store,
                                    doc.getDocumentUri(),
                                    getVulnRef(vuln),
                                    CLASS_EXTERNAL_DOC_REF,
                                    null
                                );

                                final ReferenceType type = new ReferenceType(SpdxConstants.SPDX_LISTED_REFERENCE_TYPES_PREFIX + "advisory");

                                // TODO: check if already exists
                                pkg.addExternalRef(el.createExternalRef(ReferenceCategory.SECURITY, type, locator, vuln.getComment()));
                            }
                        } catch (InvalidSPDXAnalysisException e) {
                            throw new RuntimeException(e);
                        }
                    });
            }

            // Serialize
            final var outputStream = new ByteArrayOutputStream();
            store.serialize(doc.getDocumentUri(), outputStream);

            return outputStream.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    @Nullable
    private String getSource(final Advisory advisory) {
        if (advisory.getType().equals("OSV")) {
            return "https://osv.dev/vulnerability/" + advisory.getId();
        }
        if (advisory.getType().equals("NVD")) {
            return "https://nvd.nist.gov/vuln/detail/" + advisory.getId();
        }
        return null;
    }

    @Nullable
    private ISerializableModelStore createStore(DataFormat format) {
        return switch (format) {
            case JSON -> new MultiFormatStore(new InMemSpdxStore(), MultiFormatStore.Format.JSON);
            default -> null;
        };
    }

    private String getRef(final dev.lukasnehrke.vulnaware.bom.model.Component component) {
        return "REF-C" + component.getId();
    }

    private String getVulnRef(final Vulnerability vuln) {
        return "REF-" + vuln.getAdvisory().getId() + "-V" + vuln.getId();
    }
}
