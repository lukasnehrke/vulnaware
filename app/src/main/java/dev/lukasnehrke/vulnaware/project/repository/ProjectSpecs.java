package dev.lukasnehrke.vulnaware.project.repository;

import dev.lukasnehrke.vulnaware.project.model.Project;
import dev.lukasnehrke.vulnaware.user.model.User;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

public final class ProjectSpecs {

    private ProjectSpecs() {}

    public static Specification<Project> filterByUser(final User user) {
        return (root, query, builder) -> {
            final var join = root.join("members", JoinType.INNER);
            return builder.equal(join.get("user"), user);
        };
    }

    public static Specification<Project> filterByName(final String name) {
        return (root, query, builder) ->
            builder.or(
                builder.like(builder.lower(root.get("name")), "%" + name.toLowerCase() + "%"),
                builder.like(builder.lower(root.get("slug")), "%" + name.toLowerCase() + "%")
            );
    }
}
