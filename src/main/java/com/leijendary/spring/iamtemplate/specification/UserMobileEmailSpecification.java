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

import static com.leijendary.spring.iamtemplate.util.PredicateUtil.lowerEqual;
import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Builder
public class UserMobileEmailSpecification implements Specification<IamUser> {

    private final long id;
    private final String emailAddress;
    private final String countryCode;
    private final String mobileNumber;

    @Override
    public Predicate toPredicate(@NonNull final Root<IamUser> root, @NonNull final CriteriaQuery<?> criteriaQuery,
                                 @NonNull final CriteriaBuilder criteriaBuilder) {
        // Exclude deactivated users from checking the uniqueness
        final var deactivatedDatePath = root.<OffsetDateTime>get("deactivatedDate");
        final var notDeactivated = deactivatedDatePath.isNull();

        // Exclude the user with the same ID. Only check if the username is
        // same with the other users
        final var idPath = root.<Long>get("id");
        final var notId = criteriaBuilder.notEqual(idPath, id);

        // Starting predicates
        final var predicates = new ArrayList<>(asList(notDeactivated, notId));
        // Predicates with OR filtering
        final var orPredicates = new ArrayList<Predicate>();

        // If there is an email address, filter by email address
        if (!isBlank(emailAddress)) {
            final var path = root.<String>get("emailAddress");
            final var predicate = lowerEqual(emailAddress, path, criteriaBuilder);

            orPredicates.add(predicate);
        }

        // Predicate for country code + mobile number
        if (!isBlank(countryCode) && !isBlank(mobileNumber)) {
            final var countryCodePath = root.<String>get("countryCode");
            final var countryCodeEqual = criteriaBuilder.equal(countryCodePath, countryCode);

            final var mobileNumberPath = root.<String>get("mobileNumber");
            final var mobileNumberEqual = criteriaBuilder.equal(mobileNumberPath, mobileNumber);

            final var countryCodeAndMobileNumber = criteriaBuilder.and(countryCodeEqual, mobileNumberEqual);

            orPredicates.add(countryCodeAndMobileNumber);
        }

        final var andPredicate = criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        final var orPredicate = criteriaBuilder.or(orPredicates.toArray(new Predicate[0]));

        return criteriaQuery.where(andPredicate, orPredicate).getRestriction();
    }
}
