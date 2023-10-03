package dev.lukasnehrke.vulnaware.bom.service.parser;

import com.github.packageurl.MalformedPackageURLException;
import com.github.packageurl.PackageURL;
import dev.lukasnehrke.vulnaware.bom.model.*;
import dev.lukasnehrke.vulnaware.util.PackageUtils;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.cyclonedx.BomParserFactory;
import org.cyclonedx.exception.ParseException;
import org.cyclonedx.model.Dependency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.StopWatch;
import org.springframework.web.multipart.MultipartFile;
import us.springett.parsers.cpe.Cpe;
import us.springett.parsers.cpe.CpeParser;
import us.springett.parsers.cpe.exceptions.CpeParsingException;

/**
 * Parses a SBOM file in CycloneDX Format.
 *
 * @see <a href="https://cyclonedx.org/docs/1.5/json/">CycloneDX Schema</a>
 * @see <a href="https://github.com/CycloneDX/cyclonedx-core-java">CycloneDX Library</a>
 */
public final class CycloneDxParser implements Parser {

    private static final Logger logger = LoggerFactory.getLogger(CycloneDxParser.class);

    @Override
    public boolean canHandle(MultipartFile file) throws ParserException {
        try {
            return BomParserFactory.looksLikeCycloneDX(file.getBytes());
        } catch (IOException ex) {
            throw new ParserException(ex);
        }
    }

    @Override
    public Bom parse(final MultipartFile file) throws ParserException {
        try {
            final var data = file.getBytes();
            final var watch = new StopWatch();

            watch.start();
            logger.debug("Parsing CycloneDX BOM '{}'", file.getName());

            final var bomParser = BomParserFactory.createParser(data);
            final var bomDx = bomParser.parse(data);

            final Bom bom = new Bom();
            bom.setFormat(BomFormat.CYCLONE_DX);
            bom.setFormatVersion(bomDx.getSpecVersion());
            bom.setDataFormat(getFormatByParser(bomParser));

            String mainRef = null;
            if (bomDx.getMetadata() != null && bomDx.getMetadata().getComponent() != null) {
                mainRef = bomDx.getMetadata().getComponent().getBomRef();
            }

            // generate a map of type <BomRef, Component>
            final Map<String, Component> components = bomDx
                .getComponents()
                .stream()
                .collect(Collectors.toMap(org.cyclonedx.model.Component::getBomRef, c -> createComponent(bom, c)));

            if (bomDx.getDependencies() != null) {
                // set dependencies for each component
                for (Dependency dependency : bomDx.getDependencies()) {
                    final var list = dependency.getDependencies().stream().map(d -> components.get(d.getRef())).filter(Objects::nonNull).toList();

                    // set direct dependencies
                    if (dependency.getRef().equals(mainRef)) {
                        list.forEach(c -> c.setDirect(true));
                        continue;
                    }

                    // set transitive dependencies
                    final var component = components.get(dependency.getRef());
                    if (component != null) {
                        component.setDependencies(list);
                    }
                }
            }

            bom.setComponents(List.copyOf(components.values()));

            watch.stop();
            logger.info("Parsed CycloneDX BOM: {} components ({}ms)", bom.getComponents().size(), watch.getTotalTimeMillis());

            return bom;
        } catch (IOException | ParseException ex) {
            throw new ParserException(ex);
        }
    }

    private Component createComponent(final Bom bom, final org.cyclonedx.model.Component componentDX) {
        final var component = new Component();
        component.setBom(bom);
        component.setBomRef(componentDX.getBomRef());

        final var purl = getPackageURL(componentDX);
        if (purl != null) {
            component.setPackageURL(purl.toString());
            component.setEcosystem(PackageUtils.getEcosystemByPackageURL(purl));
            PackageUtils.setOSVMappingData(purl, component);
        } else {
            component.setEcosystem(Ecosystem.OTHER);
        }

        final var cpe = getCpe(componentDX);
        if (cpe != null) {
            component.setCpe(cpe.toString());
            PackageUtils.setNvdMappingData(cpe, component);
        }

        /* set an appropriate component name */
        if (purl != null) {
            component.setName(PackageUtils.getNameByPackageURL(purl));
        } else if (notBlank(componentDX.getGroup()) && notBlank(componentDX.getName())) {
            component.setName(componentDX.getGroup() + "/" + componentDX.getName());
        } else if (notBlank(componentDX.getName())) {
            component.setName(componentDX.getName());
        } else {
            component.setName("(unknown)");
        }

        /* set an appropriate component version */
        if (purl != null) {
            component.setVersion(purl.getVersion());
        } else {
            component.setVersion(componentDX.getVersion());
        }

        return component;
    }

    @Nullable
    private PackageURL getPackageURL(final org.cyclonedx.model.Component componentDX) {
        if (componentDX.getPurl() == null || componentDX.getPurl().isBlank()) return null;
        try {
            return new PackageURL(componentDX.getPurl());
        } catch (MalformedPackageURLException ex) {
            logger.warn("Failed to parse purl: {}", componentDX.getPurl());
            return null;
        }
    }

    @Nullable
    private Cpe getCpe(final org.cyclonedx.model.Component componentDX) {
        if (componentDX.getCpe() == null || componentDX.getCpe().isBlank()) return null;
        try {
            return CpeParser.parse(componentDX.getCpe());
        } catch (CpeParsingException ex) {
            logger.warn("Failed to parse cpe: {}", componentDX.getCpe());
            return null;
        }
    }

    @Nullable
    private DataFormat getFormatByParser(final org.cyclonedx.parsers.Parser parser) {
        if (parser instanceof org.cyclonedx.parsers.JsonParser) {
            return DataFormat.JSON;
        }
        if (parser instanceof org.cyclonedx.parsers.XmlParser) {
            return DataFormat.XML;
        }
        return null;
    }

    private boolean notBlank(@Nullable final String text) {
        return text != null && !text.isBlank();
    }
}
