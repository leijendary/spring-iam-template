package com.leijendary.spring.template.iam.core.extension

import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.Path
import jakarta.persistence.criteria.Predicate

fun lowerLike(query: String, path: Path<String>, criteriaBuilder: CriteriaBuilder): Predicate {
    val lowerQuery = query.lowercase()
    val lowerPath = criteriaBuilder.lower(path)

    return criteriaBuilder.like(lowerPath, "%$lowerQuery%")
}

fun lowerEqual(value: String, path: Path<String>, criteriaBuilder: CriteriaBuilder): Predicate {
    val lowerValue = value.lowercase()
    val lowerPath = criteriaBuilder.lower(path)

    return criteriaBuilder.equal(lowerPath, lowerValue)
}
