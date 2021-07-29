package com.leijendary.spring.iamtemplate.specification;

import com.leijendary.spring.iamtemplate.model.IamUser;
import lombok.Builder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.OffsetDateTime;
import java.util.ArrayList;

import static com.leijendary.spring.iamtemplate.util.PredicateUtil.lowerLike;
import static javax.persistence.criteria.JoinType.LEFT;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Builder
public class UserListSpecification implements Specification<IamUser> {

    private final String query;
    private final boolean excludeWithAccounts;
    private final boolean excludeDeactivated;

    @Override
    public Predicate toPredicate(@NonNull final Root<IamUser> root, @NonNull final CriteriaQuery<?> criteriaQuery,
                                 @NonNull final CriteriaBuilder criteriaBuilder) {
        final var predicates = new ArrayList<Predicate>();

        if (!isBlank(query)) {
            // First Name lowercase like
            final var firstName = root.<String>get("firstName");
            final var firstNameLike = lowerLike(query, firstName, criteriaBuilder);

            // Middle Name lowercase like
            final var middleName = root.<String>get("middleName");
            final var middleNameLike = lowerLike(query, middleName, criteriaBuilder);

            // Last Name lowercase like
            final var lastName = root.<String>get("lastName");
            final var lastNameLike = lowerLike(query, lastName, criteriaBuilder);

            // Combine all fields above in an OR criteria
            final var orPredicate = criteriaBuilder.or(firstNameLike, middleNameLike, lastNameLike);

            predicates.add(orPredicate);
        }

        // Option to exclude users with accounts. Normally users with accounts are those who has
        // subscriptions or has multiple users
        if (excludeWithAccounts) {
            final var accountJoin = root.join("account", LEFT);
            final var nullAccount = accountJoin.isNull();

            predicates.add(nullAccount);
        }

        // Option to exclude users that are deactivated
        if (excludeDeactivated) {
            final var deactivatedDate = root.<OffsetDateTime>get("deactivatedDate");
            final var nullDeactivatedDate = deactivatedDate.isNull();

            predicates.add(nullDeactivatedDate);
        }

        // Combine all predicates into a AND criteria
        final var andPredicate = criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        return criteriaQuery.where(andPredicate).getRestriction();
    }
}
