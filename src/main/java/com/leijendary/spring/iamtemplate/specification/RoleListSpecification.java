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

    private final String column1;

    @Override
    public Predicate toPredicate(@NonNull final Root<IamRole> root, final CriteriaQuery<?> criteriaQuery,
                                 final CriteriaBuilder criteriaBuilder) {
        if (isBlank(this.column1)) {
            return criteriaQuery.where().getRestriction();
        }

        final var column1 = root.<String>get("column1");
        final var lowerColumn1 = criteriaBuilder.lower(column1);
        final var like = criteriaBuilder.like(lowerColumn1, "%" + this.column1.toLowerCase() + "%");

        return criteriaQuery.where(like).getRestriction();
    }
}
