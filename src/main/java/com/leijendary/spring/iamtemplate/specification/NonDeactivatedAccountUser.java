package com.leijendary.spring.iamtemplate.specification;

import com.leijendary.spring.iamtemplate.model.IamAccount;
import com.leijendary.spring.iamtemplate.model.IamUser;
import lombok.Builder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.OffsetDateTime;

@Builder
public class NonDeactivatedAccountUser implements Specification<IamUser> {

    private final long userId;

    @Override
    public Predicate toPredicate(@NonNull final Root<IamUser> root, @NonNull final CriteriaQuery<?> criteriaQuery,
                                 @NonNull final CriteriaBuilder criteriaBuilder) {
        // User's deactivated status
        final var userDeactivatedDatePath = root.<OffsetDateTime>get("deactivatedDate");
        final var userNotDeactivated = userDeactivatedDatePath.isNull();

        // User's id filtering
        final var idPath = root.<Long>get("id");
        final var idEquals = criteriaBuilder.equal(idPath, userId);

        // Account's deactivated status
        final var accountPath = root.<IamAccount>get("account");
        final var accountIsNull = accountPath.isNull();
        final var accountDeactivatedDatePath = accountPath.<OffsetDateTime>get("deactivatedDate");
        final var accountNotDeactivated = accountDeactivatedDatePath.isNull();
        final var accountIsNullOrNotDeactivated = criteriaBuilder.or(accountIsNull, accountNotDeactivated);

        return criteriaQuery
                .where(userNotDeactivated, idEquals, accountIsNullOrNotDeactivated)
                .getRestriction();
    }
}
