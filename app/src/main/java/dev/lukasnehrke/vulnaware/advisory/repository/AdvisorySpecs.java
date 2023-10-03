package dev.lukasnehrke.vulnaware.advisory.repository;

import dev.lukasnehrke.vulnaware.advisory.model.Advisory;
import dev.lukasnehrke.vulnaware.project.model.Project;
import org.springframework.data.jpa.domain.Specification;

public final class AdvisorySpecs {

    private AdvisorySpecs() {}

    public static Specification<Advisory> filterByProject(final Project project) {
        return (root, query, builder) -> {
            return builder.equal(root.get("project"), project);
        };
    }
}
