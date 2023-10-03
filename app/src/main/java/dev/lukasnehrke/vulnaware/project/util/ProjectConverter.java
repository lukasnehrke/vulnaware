package dev.lukasnehrke.vulnaware.project.util;

import dev.lukasnehrke.vulnaware.project.model.Project;
import dev.lukasnehrke.vulnaware.project.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectConverter implements Converter<String, Project> {

    private final ProjectService projectService;

    @Nullable
    @Override
    public Project convert(final String source) {
        return projectService.findByIdentifier(source);
    }
}
