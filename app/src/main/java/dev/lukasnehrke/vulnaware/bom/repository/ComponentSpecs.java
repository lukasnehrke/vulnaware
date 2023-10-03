package dev.lukasnehrke.vulnaware.bom.repository;

import dev.lukasnehrke.vulnaware.bom.model.Bom;
import dev.lukasnehrke.vulnaware.bom.model.Component;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

public final class ComponentSpecs {

    private ComponentSpecs() {}

    public static Specification<Component> filterByBom(final Bom bom) {
        return new Specification<>() {
            @Nullable
            @Override
            public Predicate toPredicate(final Root<Component> root, final CriteriaQuery<?> query, final CriteriaBuilder builder) {
                return builder.equal(root.get("bom"), bom);
            }
        };
    }

    public static Specification<Component> filterByName(final String name) {
        return new Specification<>() {
            @Nullable
            @Override
            public Predicate toPredicate(final Root<Component> root, final CriteriaQuery<?> query, final CriteriaBuilder builder) {
                return builder.or(builder.like(builder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
        };
    }

    public static Specification<Component> filterByDirect(final boolean direct) {
        return new Specification<>() {
            @Nullable
            @Override
            public Predicate toPredicate(final Root<Component> root, final CriteriaQuery<?> query, final CriteriaBuilder builder) {
                return builder.equal(root.get("direct"), direct);
            }
        };
    }
}
