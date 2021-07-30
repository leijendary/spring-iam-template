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

        // If there is an email address, filter by email address
        if (!isBlank(emailAddress)) {
            final var path = root.<String>get("emailAddress");
            final var predicate = lowerEqual(emailAddress, path, criteriaBuilder);

            predicates.add(predicate);
        }

        // If there is a country code, filter the country code.
        // This is normally partnered up with mobile number
        if (!isBlank(countryCode)) {
            final var path = root.<String>get("countryCode");
            final var predicate = criteriaBuilder.equal(path, countryCode);

            predicates.add(predicate);
        }

        // If there is a mobile number, filter the mobile number/
        // This is normally partnered up with country code
        if (!isBlank(mobileNumber)) {
            final var path = root.<String>get("mobileNumber");
            final var predicate = criteriaBuilder.equal(path, mobileNumber);

            predicates.add(predicate);
        }

        // Combine all predicates into a AND criteria
        final var andPredicate = criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        return criteriaQuery.where(andPredicate).getRestriction();
    }
}
