package dev.lukasnehrke.vulnaware.bom.service;

import static java.util.stream.Collectors.summingInt;

import dev.lukasnehrke.vulnaware.activity.model.type.UploadActivity;
import dev.lukasnehrke.vulnaware.activity.service.ActivityService;
import dev.lukasnehrke.vulnaware.bom.model.Bom;
import dev.lukasnehrke.vulnaware.bom.model.Component;
import dev.lukasnehrke.vulnaware.bom.model.History;
import dev.lukasnehrke.vulnaware.bom.model.Metrics;
import dev.lukasnehrke.vulnaware.bom.repository.BomRepository;
import dev.lukasnehrke.vulnaware.bom.repository.ComponentRepository;
import dev.lukasnehrke.vulnaware.bom.service.parser.CycloneDxParser;
import dev.lukasnehrke.vulnaware.bom.service.parser.Parser;
import dev.lukasnehrke.vulnaware.bom.service.parser.ParserException;
import dev.lukasnehrke.vulnaware.bom.service.parser.spdx.SpdxJsonParser;
import dev.lukasnehrke.vulnaware.bom.service.parser.spdx.SpdxTagParser;
import dev.lukasnehrke.vulnaware.bom.service.parser.spdx.SpdxXmlParser;
import dev.lukasnehrke.vulnaware.project.model.Project;
import dev.lukasnehrke.vulnaware.project.repository.ProjectRepository;
import dev.lukasnehrke.vulnaware.security.UserDetailsImpl;
import dev.lukasnehrke.vulnaware.storage.service.AssetService;
import dev.lukasnehrke.vulnaware.user.service.UserService;
import dev.lukasnehrke.vulnaware.util.ResourceNotFoundProblem;
import dev.lukasnehrke.vulnaware.vulnerability.repository.VulnerabilityRepository;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

@Service
@RequiredArgsConstructor
public class BomService {

    private static final List<Parser> parsers = List.of(new SpdxJsonParser(), new SpdxXmlParser(), new SpdxTagParser(), new CycloneDxParser());

    private static final Logger logger = LoggerFactory.getLogger(BomService.class);
    private final ProjectRepository projects;
    private final BomRepository boms;
    private final ComponentRepository components;
    private final VulnerabilityRepository vulns;
    private final AssetService assetService;
    private final ActivityService activityService;
    private final UserService userService;
    private final ParserService parserService;

    @Transactional(readOnly = true)
    public Page<Bom> findBoms(final Project project, @Nullable final String filter, final Pageable pageable) {
        if (filter == null) {
            return boms.findAllByProject(project, pageable);
        }
        return boms.findAllByProjectAndTagContainingIgnoreCase(project, filter, pageable);
    }

    @Transactional(readOnly = true)
    public Bom findBomOrThrow(final Project project, final String tag) {
        return boms.findByProjectAndTag(project, tag).orElseThrow(() -> new ResourceNotFoundProblem("Bom", tag));
    }

    @Transactional(readOnly = true)
    public Component findComponentOrThrow(final Project project, final Long id) {
        return components.findById(id).orElseThrow(() -> new ResourceNotFoundProblem("Component", id));
    }

    @Transactional
    public Bom createBom(
        final UserDetailsImpl userDetails,
        Project project,
        String tag,
        final MultipartFile file,
        @Nullable final String description
    ) {
        try {
            tag = tag.toLowerCase().trim();

            // parse sbom file
            var bom = parseBOM(file);
            bom.setTag(tag);
            bom.setProject(project);
            bom.setDescription(description != null && !description.isBlank() ? description.trim() : null);

            // set packages
            bom.getComponents().forEach(parserService::createPackage);

            // versioning process
            final Optional<Bom> existing = boms.findByProjectAndTag(project, tag);
            if (existing.isPresent()) {
                logger.info("Uploading new version of bom '{}'", existing.get().getId());

                bom.setId(existing.get().getId());
                bom.setCreatedAt(existing.get().getCreatedAt());
                bom.setHistory(existing.get().getHistory());

                // remove affected components from vulnerabilities
                vulns.saveAll(existing.get().getVulnerabilities().stream().peek(v -> v.getComponents().clear()).toList());
            }

            // set metrics
            final var metrics = new Metrics();
            metrics.setDependencyCount(bom.getComponents().stream().collect(Collectors.groupingBy(Component::getEcosystem, summingInt(c -> 1))));
            bom.setMetrics(metrics);

            // set history
            final var history = new History();
            history.setDate(new Date());
            history.setComponents(bom.getComponents().size());
            if (existing.isPresent()) {
                bom.getHistory().add(history);
            } else {
                final var def = new History();
                def.setDate(project.getCreatedAt());
                def.setComponents(0);
                bom.setHistory(List.of(def, history));
            }

            // upload bom to persistent storage
            if (bom.getDataFormat() != null) {
                final var location = String.format("%s/%s.%s", project.getId().toString(), tag, bom.getDataFormat().getExtension());
                final var asset = assetService.createAsset(location, file);
                bom.setAsset(asset);
            }

            // set main bom if not set
            if (project.getMain() == null) {
                project.setMain(bom);

                project = projects.save(project);
                bom = project.getMain();
            } else {
                bom = boms.save(bom);
            }

            // create upload activity
            final var activity = new UploadActivity();
            activity.setUser(userService.getReference((userDetails)));
            activity.setProject(project);
            activity.setTag(tag);
            activityService.create(activity);

            logger.info("Created bom '{}' for project '{}' ({})", tag, project.getSlug(), project.getId());
            return bom;
        } catch (ParserException ex) {
            logger.warn("Failed to parse SBOM", ex);
            throw Problem
                .builder()
                .withTitle("Invalid SBOM")
                .withDetail("The provided SBOM could not be parsed")
                .withStatus(Status.BAD_REQUEST)
                .build();
        }
    }

    public Bom parseBOM(final MultipartFile file) throws ParserException {
        for (final Parser parser : parsers) {
            if (parser.canHandle(file)) {
                logger.info("Parsing SBOM with '{}'", parser.getClass().getSimpleName());
                return parser.parse(file);
            }
        }
        throw new ParserException("No bom parser for file " + file.getName());
    }
}
