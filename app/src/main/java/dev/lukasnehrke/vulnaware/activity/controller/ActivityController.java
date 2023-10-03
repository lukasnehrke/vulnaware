package dev.lukasnehrke.vulnaware.activity.controller;

import dev.lukasnehrke.vulnaware.activity.model.Activity;
import dev.lukasnehrke.vulnaware.activity.repository.ActivityRepository;
import dev.lukasnehrke.vulnaware.project.model.Project;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projects/{project}")
@AllArgsConstructor
public final class ActivityController {

    private final ActivityRepository activities;

    @GetMapping("/activities")
    List<Activity> getActivities(final Project project) {
        final var pageable = PageRequest.of(0, 100, Sort.by("createdAt").descending());
        return this.activities.findByProjectId(project.getId(), pageable);
    }
}
