package com.leijendary.spring.template.iam.specification

import com.leijendary.spring.template.iam.api.v1.model.UserExclusionQueryRequest
import com.leijendary.spring.template.iam.core.extension.concat
import com.leijendary.spring.template.iam.core.extension.lowerLike
import com.leijendary.spring.template.iam.entity.Account
import com.leijendary.spring.template.iam.entity.User
import jakarta.persistence.criteria.*
import jakarta.persistence.criteria.JoinType.LEFT
import org.springframework.data.jpa.domain.Specification

class UserListSpecification(
    private val query: String? = null,
    private val exclusionQuery: UserExclusionQueryRequest
) : Specification<User> {
    override fun toPredicate(
        root: Root<User>,
        criteriaQuery: CriteriaQuery<*>,
        criteriaBuilder: CriteriaBuilder
    ): Predicate {
        val predicates = mutableListOf<Predicate>()

        if (!query.isNullOrBlank()) {
            // First name and last name
            val names = listOf<Path<String>>(root.get("firstName"), root.get("lastName"))
            val concatenated = criteriaBuilder.concat(" ", *names.toTypedArray())
            val namesLike = criteriaBuilder.lowerLike(concatenated, query)

            // Email
            val email = root.get<String>("email")
            val emailLike = criteriaBuilder.lowerLike(email, query)

            // (First name and last name) or (email)
            val namesOrEmailLike = criteriaBuilder.or(namesLike, emailLike)

            predicates.add(namesOrEmailLike)
        }

        if (exclusionQuery.exclude.withAccounts) {
            val nullAccount = root.join<Account, User>("account", LEFT).isNull

            predicates.add(nullAccount)
        }

        return criteriaBuilder.and(*predicates.toTypedArray())
    }
}
