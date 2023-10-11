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
            val fields = listOf<Path<String>>(root.get("firstName"), root.get("lastName"))
            val concatenated = criteriaBuilder.concat(" ", *fields.toTypedArray())
            val like = criteriaBuilder.lowerLike(concatenated, query)

            predicates.add(like)
        }

        if (exclusionQuery.exclude.withAccounts) {
            val nullAccount = root.join<Account, User>("account", LEFT).isNull

            predicates.add(nullAccount)
        }

        return criteriaBuilder.and(*predicates.toTypedArray())
    }
}
