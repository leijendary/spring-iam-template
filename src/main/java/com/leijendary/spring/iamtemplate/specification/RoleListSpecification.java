package com.leijendary.spring.iamtemplate.specification;

import com.leijendary.spring.iamtemplate.model.IamRole;
import lombok.Builder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Builder
public class RoleListSpecification implements Specification<IamRole> {

    private final String query;

    @Override
    public Predicate toPredicate(@NonNull final Root<IamRole> root, @NonNull final CriteriaQuery<?> criteriaQuery,
                                 @NonNull final CriteriaBuilder criteriaBuilder) {
        if (isBlank(this.query)) {
            return criteriaQuery.where().getRestriction();
        }

        final var lowerQuery = query.toLowerCase();

        // Role lowercase like
        final var role = root.<String>get("role");
        final var lowerRole = criteriaBuilder.lower(role);
        final var roleLike = criteriaBuilder.like(lowerRole, "%" + lowerQuery + "%");

        // Description lowercase like
        final var description = root.<String>get("description");
        final var lowerDescription = criteriaBuilder.lower(description);
        final var descriptionLike = criteriaBuilder.like(lowerDescription, "%" + lowerQuery + "%");

        // Combine the predicates into an "OR" condition
        final var predicate = criteriaBuilder.or(roleLike, descriptionLike);

        return criteriaQuery.where(predicate).getRestriction();
    }
}
