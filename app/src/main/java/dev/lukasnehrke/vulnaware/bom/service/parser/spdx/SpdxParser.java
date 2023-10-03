package dev.lukasnehrke.vulnaware.bom.service.parser.spdx;

import com.github.packageurl.MalformedPackageURLException;
import com.github.packageurl.PackageURL;
import dev.lukasnehrke.vulnaware.bom.model.*;
import dev.lukasnehrke.vulnaware.bom.service.parser.Parser;
import dev.lukasnehrke.vulnaware.bom.service.parser.ParserException;
import dev.lukasnehrke.vulnaware.util.PackageUtils;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spdx.library.InvalidSPDXAnalysisException;
import org.spdx.library.SpdxConstants;
import org.spdx.library.model.ExternalRef;
import org.spdx.library.model.SpdxDocument;
import org.spdx.library.model.SpdxModelFactory;
import org.spdx.library.model.SpdxPackage;
import org.spdx.storage.ISerializableModelStore;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

public abstract class SpdxParser implements Parser {

    private static final Logger logger = LoggerFactory.getLogger(SpdxParser.class);

    protected abstract DataFormat getDataFormat();

    protected abstract ISerializableModelStore createStore();

    @Override
    public Bom parse(final MultipartFile file) throws ParserException {
        try (final ISerializableModelStore store = createStore()) {
            logger.info("Parsing SPDX file '{}'", file.getOriginalFilename());

            final String documentUri = store.deSerialize(file.getInputStream(), true);
            final SpdxDocument document = new SpdxDocument(store, documentUri, null, false);

            final Bom bom = new Bom();
            bom.setFormat(BomFormat.SPDX);
            bom.setFormatVersion(document.getSpecVersion());
            bom.setDataFormat(getDataFormat());

            bom.setComponents(
                SpdxModelFactory
                    .getElements(store, document.getDocumentUri(), null, SpdxPackage.class)
                    .map(SpdxPackage.class::cast)
                    .map(this::createComponent)
                    .filter(Objects::nonNull)
                    .peek(pkg -> pkg.setBom(bom))
                    .toList()
            );

            logger.info("Found {} components", bom.getComponents().size());
            return bom;
        } catch (final Exception ex) {
            throw new ParserException(ex);
        }
    }

    @Nullable
    private Component createComponent(final SpdxPackage pkg) {
        try {
            final Component component = new Component();

            component.setName(pkg.getName().orElse(null));
            component.setBomRef(pkg.getId());

            for (final ExternalRef ref : pkg.getExternalRefs()) {
                // Package URL
                if (ref.getReferenceType().getIndividualURI().equals(SpdxConstants.SPDX_LISTED_REFERENCE_TYPES_PREFIX + "purl")) {
                    try {
                        final PackageURL purl = new PackageURL(ref.getReferenceLocator());

                        component.setName(PackageUtils.getNameByPackageURL(purl));
                        component.setVersion(purl.getVersion());
                        component.setPackageURL(purl.toString());
                        component.setEcosystem(PackageUtils.getEcosystemByPackageURL(purl));
                        PackageUtils.setOSVMappingData(purl, component);
                    } catch (MalformedPackageURLException ex) {
                        logger.warn("Package has invalid package url", ex);
                    }
                }
            }

            if (component.getName() == null) {
                component.setName(component.getBomRef());
            }

            if (component.getEcosystem() == null) {
                component.setEcosystem(Ecosystem.OTHER);
            }

            return component;
        } catch (InvalidSPDXAnalysisException ex) {
            logger.warn("Skipping invalid component '{}'", pkg.getId());
            return null;
        }
    }
}
