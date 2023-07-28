package com.leijendary.spring.template.iam.core.validator

import com.leijendary.spring.template.iam.core.exception.ResourceNotFoundException
import com.leijendary.spring.template.iam.core.model.Countries
import org.springframework.stereotype.Component

private val sourceCode = listOf("data", "Country", "code")

@Component
class CountryValidator(private val countries: Countries) {
    fun validateCode(code: String): String {
        return countries[code] ?: throw ResourceNotFoundException(sourceCode, code)
    }
}
