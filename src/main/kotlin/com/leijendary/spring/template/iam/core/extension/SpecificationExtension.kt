package com.leijendary.spring.template.iam.core.extension

import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.Path
import jakarta.persistence.criteria.Predicate

fun Path<String>.lowerLike(query: String, criteriaBuilder: CriteriaBuilder): Predicate {
    val lowerQuery = query.lowercase()
    val lowerPath = criteriaBuilder.lower(this)

    return criteriaBuilder.like(lowerPath, "%$lowerQuery%")
}

fun Path<String>.lowerEqual(value: String, criteriaBuilder: CriteriaBuilder): Predicate {
    val lowerValue = value.lowercase()
    val lowerPath = criteriaBuilder.lower(this)

    return criteriaBuilder.equal(lowerPath, lowerValue)
}
