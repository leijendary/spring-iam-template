package com.leijendary.spring.template.iam.strategy

import com.leijendary.spring.template.iam.core.exception.StatusException
import com.leijendary.spring.template.iam.core.extension.logger
import com.leijendary.spring.template.iam.model.SocialProvider
import com.leijendary.spring.template.iam.model.SocialResult
import org.springframework.http.HttpStatus
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtException

abstract class SocialVerificationStrategy {
    private val log = logger()

    abstract val provider: SocialProvider
    abstract val decoder: JwtDecoder

    abstract fun mapper(jwt: Jwt): SocialResult

    fun verify(token: String): SocialResult {
        val jwt = try {
            decoder.decode(token)
        } catch (exception: JwtException) {
            val message = exception.message!!

            log.warn(message)

            if (message.contains("expired")) {
                throw statusException("access.expired")
            }

            throw statusException("access.invalid")
        }

        return mapper(jwt)
    }

    private fun statusException(code: String): StatusException {
        val source = listOf("header", "authorization")

        return StatusException(source, code, HttpStatus.UNAUTHORIZED)
    }
}
