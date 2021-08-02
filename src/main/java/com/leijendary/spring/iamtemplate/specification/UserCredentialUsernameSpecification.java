package com.leijendary.spring.iamtemplate.specification;

import com.leijendary.spring.iamtemplate.model.IamUserCredential;
import lombok.Builder;
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
public class UserCredentialUsernameSpecification implements Specification<IamUserCredential> {

    private final String username;

    @Override
    public Predicate toPredicate(@NonNull final Root<IamUserCredential> root,
                                 @NonNull final CriteriaQuery<?> criteriaQuery,
                                 @NonNull final CriteriaBuilder criteriaBuilder) {
        final var predicates = new ArrayList<Predicate>();

        final var userJoin = root.join("user", INNER);
        final var userDeactivatedDatePath = userJoin.<OffsetDateTime>get("deactivatedDate");
        final var userNotDeactivated = userDeactivatedDatePath.isNull();

        // The user should be deactivated
        predicates.add(userNotDeactivated);

        final var usernamePath = root.<String>get("username");
        final var usernameEquals = criteriaBuilder.equal(usernamePath, username);

        // Username should be equals to
        predicates.add(usernameEquals);

        return criteriaQuery
                .where(predicates.toArray(new Predicate[0]))
                .getRestriction();
    }
}
