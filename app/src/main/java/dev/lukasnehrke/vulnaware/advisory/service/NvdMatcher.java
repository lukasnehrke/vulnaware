package dev.lukasnehrke.vulnaware.advisory.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.lukasnehrke.vulnaware.advisory.model.NvdAdvisory;
import dev.lukasnehrke.vulnaware.bom.model.Component;
import dev.lukasnehrke.vulnaware.util.schema.nvd.Advisory;
import dev.lukasnehrke.vulnaware.util.schema.nvd.Match;
import dev.lukasnehrke.vulnaware.util.schema.nvd.Node;
import dev.lukasnehrke.vulnaware.util.version.GenericVersion;
import dev.lukasnehrke.vulnaware.util.version.Version;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.springett.parsers.cpe.CpeParser;
import us.springett.parsers.cpe.exceptions.CpeParsingException;

public final class NvdMatcher {

    private static final Logger logger = LoggerFactory.getLogger(NvdMatcher.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    private final Advisory advisory;

    public NvdMatcher(final NvdAdvisory advisory) throws Exception {
        this.advisory = mapper.readValue(advisory.getNvdData(), Advisory.class);
    }

    NvdMatcher(final String rawData) throws Exception {
        this.advisory = mapper.readValue(rawData, Advisory.class);
    }

    public List<Component> matchAgainst(final List<Component> components) throws Exception {
        try {
            final var result = new HashSet<Component>();
            for (final var config : advisory.getCve().getConfigurations()) {
                if (config.isNegate()) {
                    logger.warn("Ignoring root negation for '{}'", advisory.getCve().getId());
                    continue;
                }

                if (config.getOperator().equals("AND")) {
                    boolean vulnerable = true;
                    Set<Component> set = new HashSet<>();

                    for (final var node : config.getNodes()) {
                        final var list = matchesNode(node, components);
                        if (!list.vulnerable) {
                            vulnerable = false;
                            break;
                        }
                        set.addAll(list.affected);
                    }

                    if (vulnerable) {
                        result.addAll(set);
                    }

                    continue;
                }

                // OR
                boolean vulnerable = false;
                Set<Component> set = new HashSet<>();
                for (final var node : config.getNodes()) {
                    final var list = matchesNode(node, components);
                    if (list.vulnerable) {
                        vulnerable = true;
                        set.addAll(list.affected);
                    }
                }

                if (vulnerable) {
                    result.addAll(set);
                }
            }
            return result.stream().toList();
        } catch (CpeParsingException ex) {
            logger.warn("Failed to parse CPE", ex);
            return new ArrayList<>();
        }
    }

    private Result matchesNode(Node node, final List<Component> list) throws CpeParsingException {
        final var result = new Result(false, new HashSet<>());

        if (node.getOperator().equals("AND")) {
            logger.warn("{}: Ignoring AND operator", advisory.getCve().getId());
            return result;
        }

        if (node.isNegate()) {
            logger.warn("{}: Ignoring negated node", advisory.getCve().getId());
            return result;
        }

        // OR
        for (final var match : node.getCpeMatch()) {
            final var matchResult = matchCPE(match, list);
            result.affected.addAll(matchResult.getAffected());
            if (matchResult.isVulnerable()) {
                result.vulnerable = true;
            }
        }

        return result;
    }

    private Result matchCPE(final Match match, final List<Component> list) throws CpeParsingException {
        final var result = new Result(false, new HashSet<>());
        final var cpe = CpeParser.parse(match.getCriteria());

        System.out.println("Criteria: " + cpe.toString());

        for (final var item : list) {
            if (item.getCpe() == null) continue;

            final var itemCpe = CpeParser.parse(item.getCpe());
            if (!cpe.matches(itemCpe)) {
                System.out.println("--- does not match " + itemCpe.toString());
                continue;
            }

            final var itemVersion = new GenericVersion(itemCpe.getVersion());
            if (match.getVersionStartIncluding() != null && itemVersion.isBefore(match.getVersionStartIncluding())) {
                System.out.println("--- Ignoring version " + itemVersion + " < " + match.getVersionStartIncluding());
                continue;
            }

            if (match.getVersionStartExcluding() != null && itemVersion.isBeforeEqual(match.getVersionStartExcluding())) {
                System.out.println("--- Ignoring version " + itemVersion + " <= " + match.getVersionStartExcluding());
                continue;
            }

            if (match.getVersionEndIncluding() != null && itemVersion.isAfter(match.getVersionEndIncluding())) {
                System.out.println("--- Ignoring version " + itemVersion + " > " + match.getVersionEndIncluding());
                continue;
            }

            if (match.getVersionEndExcluding() != null && itemVersion.isAfterEqual(match.getVersionEndExcluding())) {
                System.out.println("--- Ignoring version " + itemVersion + " >= " + match.getVersionEndExcluding());
                continue;
            }

            System.out.println("--- vulnerable!");
            result.vulnerable = true;

            if (match.isVulnerable()) {
                result.affected.add(item);
            }
        }

        return result;
    }

    @Data
    @AllArgsConstructor
    private static class Result {

        boolean vulnerable;

        Set<Component> affected;
    }
}
