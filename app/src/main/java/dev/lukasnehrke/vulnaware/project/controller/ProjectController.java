package dev.lukasnehrke.vulnaware.project.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.fasterxml.jackson.annotation.JsonView;
import dev.lukasnehrke.vulnaware.bom.model.Bom;
import dev.lukasnehrke.vulnaware.project.dto.ProjectCreation;
import dev.lukasnehrke.vulnaware.project.model.Project;
import dev.lukasnehrke.vulnaware.project.model.ProjectMember;
import dev.lukasnehrke.vulnaware.project.service.ProjectService;
import dev.lukasnehrke.vulnaware.security.UserDetailsImpl;
import dev.lukasnehrke.vulnaware.user.CurrentUser;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projects")
@AllArgsConstructor
class ProjectController {

    private final ProjectService projectService;

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    @JsonView(Bom.PublicView.class)
    List<Project> getProjects(@CurrentUser UserDetailsImpl userDetails, @Nullable @RequestParam(required = false) final String name) {
        return projectService.findAll(userDetails, name);
    }

    @GetMapping(value = "{identifier}", produces = APPLICATION_JSON_VALUE)
    @JsonView(Bom.PublicView.class)
    Project find(final @PathVariable String identifier) {
        return projectService.findByIdentifier(identifier);
    }

    @PostMapping(produces = APPLICATION_JSON_VALUE)
    @JsonView(Bom.PublicView.class)
    Project create(@AuthenticationPrincipal final UserDetails user, final @Valid @RequestBody ProjectCreation project) {
        return projectService.createProject(user, project.getName(), project.getSlug());
    }

    @DeleteMapping(value = "/{identifier}", produces = APPLICATION_JSON_VALUE)
    ResponseEntity<?> delete(final @PathVariable String identifier) {
        final var project = projectService.findByIdentifier(identifier);
        projectService.deleteProject(project);

        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/{identifier}/members", produces = APPLICATION_JSON_VALUE)
    @JsonView(Bom.PublicView.class)
    List<ProjectMember> getMembers(final @PathVariable String identifier) {
        return projectService.findMembers(identifier);
    }

    @PostMapping(value = "/{identifier}/members/{email}", produces = APPLICATION_JSON_VALUE)
    @JsonView(Bom.PublicView.class)
    ResponseEntity<?> addMember(final @PathVariable String identifier, final @PathVariable String email) {
        return ResponseEntity.ok(projectService.addMember(identifier, email));
    }

    @DeleteMapping(value = "/{identifier}/members/{email}", produces = APPLICATION_JSON_VALUE)
    @JsonView(Bom.PublicView.class)
    ResponseEntity<?> deleteMember(final @PathVariable String identifier, final @PathVariable String email) {
        return ResponseEntity.ok(projectService.deleteMember(identifier, email));
    }
}
