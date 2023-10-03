package dev.lukasnehrke.vulnaware.bom.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.fasterxml.jackson.annotation.JsonView;
import dev.lukasnehrke.vulnaware.bom.model.Bom;
import dev.lukasnehrke.vulnaware.bom.model.Component;
import dev.lukasnehrke.vulnaware.bom.repository.ComponentRepository;
import dev.lukasnehrke.vulnaware.bom.repository.ComponentSpecs;
import dev.lukasnehrke.vulnaware.bom.service.BomService;
import dev.lukasnehrke.vulnaware.bom.service.LookupService;
import dev.lukasnehrke.vulnaware.project.model.Project;
import dev.lukasnehrke.vulnaware.project.service.ProjectService;
import dev.lukasnehrke.vulnaware.security.UserDetailsImpl;
import dev.lukasnehrke.vulnaware.user.CurrentUser;
import dev.lukasnehrke.vulnaware.vulnerability.service.VulnerabilityService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/projects/{project}/bom")
@AllArgsConstructor
class BomController {

    private final BomService bomService;
    private final VulnerabilityService vulnService;
    private final LookupService lookupService;
    private final ComponentRepository components;

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    @JsonView(Bom.PublicView.class)
    Page<Bom> getBoms(@PathVariable final Project project, @Nullable @RequestParam(required = false) final String filter) {
        return bomService.findBoms(project, filter, PageRequest.of(0, 10));
    }

    @GetMapping(value = "/{tag}", produces = APPLICATION_JSON_VALUE)
    @JsonView(Bom.PublicView.class)
    Bom getBom(@PathVariable final Project project, @PathVariable final String tag) {
        return bomService.findBomOrThrow(project, tag);
    }

    @PostMapping(value = "/{tag}", produces = APPLICATION_JSON_VALUE)
    @JsonView(Bom.PublicView.class)
    ResponseEntity<?> createBOM(
        @CurrentUser final UserDetailsImpl userDetails,
        @PathVariable final Project project,
        @PathVariable final String tag,
        @RequestParam("description") final String description,
        @RequestParam("file") final MultipartFile file
    ) {
        final var bom = bomService.createBom(userDetails, project, tag, file, description);

        this.vulnService.analyzeLater(bom.getId()); // analyze vulnerabilities
        this.lookupService.lookupBom(bom.getId()); // lookup components

        return ResponseEntity.status(HttpStatus.CREATED).body(bom);
    }

    @GetMapping(value = "/{tag}/components", produces = APPLICATION_JSON_VALUE)
    @JsonView(Component.ListView.class)
    Page<Component> listComponents(
        @PathVariable final Project project,
        @PathVariable final String tag,
        @Nullable @RequestParam(required = false) final Boolean direct,
        @Nullable @RequestParam(required = false) final String name,
        @RequestParam(defaultValue = "0") final int page,
        @RequestParam(defaultValue = "10") final int size
    ) {
        final var bom = bomService.findBomOrThrow(project, tag);

        /* build search predicate */
        var spec = ComponentSpecs.filterByBom(bom);
        if (name != null && !name.isBlank()) {
            spec = spec.and(ComponentSpecs.filterByName(name));
        }
        if (direct != null) {
            spec = spec.and(ComponentSpecs.filterByDirect(direct));
        }

        final Sort sort = Sort.by("name").ascending();
        final Pageable paging = PageRequest.of(page, size, sort);
        final Page<Component> result = components.findAll(spec, paging);

        return result;
    }

    @GetMapping(value = "/{tag}/components/{id}", produces = APPLICATION_JSON_VALUE)
    @JsonView(Component.PublicView.class)
    List<Component> getDependencies(Project project, @PathVariable Long id) {
        return bomService.findComponentOrThrow(project, id).getDependencies();
    }
}
