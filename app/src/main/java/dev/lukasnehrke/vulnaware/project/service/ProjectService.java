package dev.lukasnehrke.vulnaware.project.service;

import dev.lukasnehrke.vulnaware.activity.model.type.ProjectCreationActivity;
import dev.lukasnehrke.vulnaware.activity.service.ActivityService;
import dev.lukasnehrke.vulnaware.project.model.Project;
import dev.lukasnehrke.vulnaware.project.model.ProjectMember;
import dev.lukasnehrke.vulnaware.project.model.ProjectMemberRole;
import dev.lukasnehrke.vulnaware.project.repository.ProjectRepository;
import dev.lukasnehrke.vulnaware.project.repository.ProjectSpecs;
import dev.lukasnehrke.vulnaware.security.UserDetailsImpl;
import dev.lukasnehrke.vulnaware.user.service.UserService;
import dev.lukasnehrke.vulnaware.util.Numbers;
import dev.lukasnehrke.vulnaware.util.ResourceNotFoundProblem;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private static final Logger logger = LoggerFactory.getLogger(ProjectService.class);
    private final UserService userService;
    private final ActivityService activityService;
    private final ProjectRepository projects;

    @Transactional(readOnly = true)
    public boolean isMember(final Authentication authentication, final Project project) {
        return project
            .getMembers()
            .stream()
            .map(member -> member.getUser().getId())
            .anyMatch(id -> id.equals(((UserDetailsImpl) authentication.getPrincipal()).getId()));
    }

    @Transactional(readOnly = true)
    public boolean isOwner(final Authentication authentication, final Project project) {
        return project
            .getMembers()
            .stream()
            .filter(member -> member.getRole() == ProjectMemberRole.OWNER)
            .map(member -> member.getUser().getId())
            .anyMatch(id -> id.equals(((UserDetailsImpl) authentication.getPrincipal()).getId()));
    }

    @Transactional(readOnly = true)
    @PostAuthorize("@projectService.isMember(authentication, returnObject)")
    public Project findByIdentifier(final String identifier) {
        if (Numbers.isLong(identifier)) {
            return projects.findById(Long.parseLong(identifier)).orElseThrow(() -> new ResourceNotFoundProblem("Project", identifier));
        }

        return projects.findBySlug(identifier).orElseThrow(() -> new ResourceNotFoundProblem("Project", identifier));
    }

    @Transactional(readOnly = true)
    @PostAuthorize("@projectService.isOwner(authentication, returnObject)")
    public Project findByIdentifierOwner(final String identifier) {
        if (Numbers.isLong(identifier)) {
            return projects.findById(Long.parseLong(identifier)).orElseThrow(() -> new ResourceNotFoundProblem("Project", identifier));
        }
        return projects.findBySlug(identifier).orElseThrow(() -> new ResourceNotFoundProblem("Project", identifier));
    }

    @Transactional(readOnly = true)
    public Project findBySlug(final String slug) {
        return projects.findBySlug(slug).orElseThrow(() -> new ResourceNotFoundProblem("Project", slug));
    }

    @Transactional(readOnly = true)
    public List<Project> findAll(final UserDetails user, @Nullable final String name) {
        var spec = ProjectSpecs.filterByUser(userService.getReference(user));

        /* filter by name or slug */
        if (name != null && !name.isBlank()) {
            spec = spec.and(ProjectSpecs.filterByName(name));
        }

        return projects.findAll(spec);
    }

    @Transactional(readOnly = true)
    public List<ProjectMember> findMembers(final String identifier) {
        return findByIdentifier(identifier).getMembers();
    }

    @Transactional
    public List<ProjectMember> addMember(String identifier, String email) {
        var project = findByIdentifierOwner(identifier);
        var user = userService.findByEmail(email).orElseThrow(() -> new ResourceNotFoundProblem("User", email));

        var member = new ProjectMember();
        member.setProject(project);
        member.setUser(user);
        member.setRole(ProjectMemberRole.MEMBER);
        project.getMembers().add(member);
        projects.save(project);

        logger.info("Added member {} to project {}", email, identifier);
        return project.getMembers();
    }

    @Transactional
    public List<ProjectMember> deleteMember(String identifier, String email) {
        var project = findByIdentifierOwner(identifier);
        var member = project
            .getMembers()
            .stream()
            .filter(m -> m.getUser().getEmail().equals(email))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundProblem("ProjectMember", email));
        if (member.getRole() == ProjectMemberRole.OWNER) throw new IllegalArgumentException("Cannot delete owner");
        project.getMembers().remove(member);
        project = projects.save(project);

        logger.info("Deleted member {} from project {}", email, identifier);
        return project.getMembers();
    }

    @Transactional
    public Project createProject(final UserDetails user, final String name, final String slug) {
        var project = new Project();
        project.setName(name);
        project.setSlug(slug);

        final var owner = new ProjectMember();
        owner.setProject(project);
        owner.setUser(userService.getReference(user));
        owner.setRole(ProjectMemberRole.OWNER);
        project.setMembers(List.of(owner));

        project = projects.save(project);

        // create activity
        final var activity = new ProjectCreationActivity();
        activity.setUser(userService.getReference(user));
        activity.setProject(project);
        activityService.create(activity);

        return project;
    }

    @Transactional
    @PreAuthorize("@projectService.isMember(authentication, #project)")
    public void deleteProject(final Project project) {
        projects.delete(project);
    }
}
