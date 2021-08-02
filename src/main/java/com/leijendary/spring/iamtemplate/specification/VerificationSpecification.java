package com.leijendary.spring.iamtemplate.specification;

import com.leijendary.spring.iamtemplate.model.IamVerification;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.OffsetDateTime;
import java.util.ArrayList;

import static javax.persistence.criteria.JoinType.INNER;

@Builder
public class VerificationSpecification implements Specification<IamVerification> {

    @Getter
    private final long id;

    private final String code;
    private final String type;

    @Override
    public Predicate toPredicate(@NonNull final Root<IamVerification> root,
                                 @NonNull final CriteriaQuery<?> criteriaQuery,
                                 @NonNull final CriteriaBuilder criteriaBuilder) {
        final var predicates = new ArrayList<Predicate>();

        final var userJoin = root.join("user", INNER);
        final var userDeactivatedDatePath = userJoin.<OffsetDateTime>get("deactivatedDate");
        final var userNotDeactivated = userDeactivatedDatePath.isNull();

        // The user should be deactivated
        predicates.add(userNotDeactivated);

        final var idPath = root.<Long>get("id");
        final var idEquals = criteriaBuilder.equal(idPath, id);

        // Id should be equals to
        predicates.add(idEquals);

        final var codePath = root.<String>get("code");
        final var codeEquals = criteriaBuilder.equal(codePath, code);

        // Code should be equals to
        predicates.add(codeEquals);

        final var typePath = root.<String>get("type");
        final var typeEquals = criteriaBuilder.equal(typePath, type);

        // Type should be equals to
        predicates.add(typeEquals);

        return criteriaQuery
                .where(predicates.toArray(new Predicate[0]))
                .getRestriction();
    }
}
