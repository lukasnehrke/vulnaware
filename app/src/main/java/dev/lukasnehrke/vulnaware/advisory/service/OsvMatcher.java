package dev.lukasnehrke.vulnaware.advisory.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.lukasnehrke.vulnaware.advisory.model.OsvAdvisory;
import dev.lukasnehrke.vulnaware.bom.model.Component;
import dev.lukasnehrke.vulnaware.util.version.GenericVersion;
import dev.lukasnehrke.vulnaware.util.version.Version;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class OsvMatcher {

    private static final Logger logger = LoggerFactory.getLogger(OsvMatcher.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    private final OsvAdvisory advisory;
    private JsonNode osvData;

    public OsvMatcher(OsvAdvisory advisory) {
        this.advisory = advisory;
    }

    public List<Component> matchAgainst(final List<Component> components) throws Exception {
        this.osvData = mapper.readTree(advisory.getOsvData());
        return components.stream().filter(c -> isCompatible(c) && isAffected(c)).toList();
    }

    private boolean isCompatible(final Component component) {
        return component.getOsvEcosystem() != null && component.getOsvName() != null && component.getVersion() != null;
    }

    private boolean isAffected(final Component c) {
        if (c.getVersion() == null) return false; // component has no version
        if (!osvData.hasNonNull("affected")) return false;

        for (final var affected : osvData.get("affected")) {
            final var pkg = affected.get("package");
            final var ecosystem = pkg.get("ecosystem").asText();
            final var name = pkg.get("name").asText();

            // incorrect package name or ecosystem
            if (!isCorrectEcosystem(c.getOsvEcosystem(), ecosystem) || !c.getOsvName().equals(name)) {
                continue;
            }

            // package version is in one if the defined ranges
            if (isInRanges(c, affected)) {
                return true;
            }

            // package version is explicitly defined in the versions array
            if (isInVersions(c, affected)) {
                return true;
            }
        }

        return false;
    }

    private boolean isCorrectEcosystem(String ecosystem, String ecosystemDb) {
        if (ecosystemDb.startsWith("Debian") && ecosystem.startsWith("Debian")) return true;
        return ecosystem.equals(ecosystemDb);
    }

    private boolean isInVersions(final Component component, final JsonNode affected) {
        if (affected.hasNonNull("versions")) {
            for (final var el : affected.get("versions")) {
                if (el.asText().equals(component.getVersion())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isInRanges(final Component component, final JsonNode affected) {
        if (component.getVersion() != null && affected.hasNonNull("ranges")) {
            for (final var range : affected.get("ranges")) {
                final var type = range.get("type").asText();

                // try to parse version for comparison
                final Version<?> version;
                try {
                    final var optionalVersion = Version.of(type, component.getOsvEcosystem(), component.getVersion());
                    if (optionalVersion.isEmpty()) {
                        logger.warn("Cannot handle osv version type: {} ({})", type, component.getOsvEcosystem());
                        continue;
                    }
                    version = optionalVersion.get();
                } catch (Exception ex) {
                    logger.warn("Failed to parse version: {} ({})", component.getVersion(), component.getOsvEcosystem());
                    continue;
                }

                if (isBeforeLimits(version, range)) {
                    String fixed = null;
                    boolean vulnerable = false;

                    for (final var event : range.get("events")) {
                        if (event.hasNonNull("introduced") && version.isAfterEqual(event.get("introduced").asText())) {
                            vulnerable = true;
                        } else if (event.hasNonNull("fixed")) {
                            fixed = event.get("fixed").asText();
                            if (version.isAfterEqual(fixed)) {
                                vulnerable = false;
                            }
                        } else if (event.hasNonNull("last_affected") && version.isAfter(event.get("last_affected").asText())) {
                            vulnerable = false;
                        }
                    }

                    if (vulnerable) {
                        component.setFixedVersion(fixed);
                        return !(version instanceof GenericVersion);
                    }
                }
            }
        }

        return false;
    }

    private boolean isBeforeLimits(final Version<?> version, final JsonNode range) {
        var implicitLimit = true;
        for (final var event : range.get("events")) {
            if (event.hasNonNull("limit")) {
                implicitLimit = false;

                final var limit = event.get("limit").asText();
                if (limit.equals("*") || version.isBefore(limit)) {
                    return true;
                }
            }
        }

        return implicitLimit;
    }
}
