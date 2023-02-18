package com.leijendary.spring.template.iam.specification

import com.leijendary.spring.template.iam.api.v1.model.UserExclusionQueryRequest
import com.leijendary.spring.template.iam.core.extension.lowerLike
import com.leijendary.spring.template.iam.entity.Account
import com.leijendary.spring.template.iam.entity.User
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.JoinType.LEFT
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification

class UserListSpecification(
    private val query: String? = null,
    private val exclusionQuery: UserExclusionQueryRequest
) : Specification<User> {
    override fun toPredicate(
        root: Root<User>,
        criteriaQuery: CriteriaQuery<*>,
        criteriaBuilder: CriteriaBuilder
    ): Predicate? {
        val predicates = mutableListOf<Predicate>()

        if (!query.isNullOrBlank()) {
            val firstName = root
                .get<String>("firstName")
                .let {
                    lowerLike(query, it, criteriaBuilder)
                }
            val middleName = root
                .get<String>("middleName")
                .let {
                    lowerLike(query, it, criteriaBuilder)
                }
            val lastName = root
                .get<String>("lastName")
                .let {
                    lowerLike(query, it, criteriaBuilder)
                }
            val orNames = criteriaBuilder.or(firstName, middleName, lastName)

            predicates.add(orNames)
        }

        if (exclusionQuery.exclude.withAccounts) {
            val nullAccount = root.join<Account, User>("account", LEFT).isNull

            predicates.add(nullAccount)
        }

        if (predicates.isEmpty()) {
            return null
        }

        return criteriaBuilder.and(*predicates.toTypedArray())
    }
}
