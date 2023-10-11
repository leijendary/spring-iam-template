package com.leijendary.spring.template.iam.core.extension

import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.Expression
import jakarta.persistence.criteria.Predicate

fun CriteriaBuilder.concat(delimiter: String, vararg expressions: Expression<String>): Expression<String> {
    return expressions.foldIndexed(expressions[0]) { index, acc, expression ->
        val isLast = expressions.size == index + 1

        if (isLast) {
            concat(acc, expression)
        } else {
            concat(expression, delimiter)
        }
    }
}

fun CriteriaBuilder.lowerLike(expression: Expression<String>, value: String): Predicate {
    val lowerValue = value.lowercase()
    val lowerExpression = lower(expression)

    return like(lowerExpression, "%$lowerValue%")
}

fun CriteriaBuilder.lowerEqual(expression: Expression<String>, value: String): Predicate {
    val lowerValue = value.lowercase()
    val lowerExpression = lower(expression)

    return equal(lowerExpression, lowerValue)
}
