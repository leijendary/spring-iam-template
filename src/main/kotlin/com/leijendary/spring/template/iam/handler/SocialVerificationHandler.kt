package com.leijendary.spring.template.iam.handler

import com.leijendary.spring.template.iam.core.exception.StatusException
import com.leijendary.spring.template.iam.core.extension.logger
import com.leijendary.spring.template.iam.entity.UserSocial.Provider
import com.leijendary.spring.template.iam.model.SocialResult
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtException

private val source = listOf("body", "token")

abstract class SocialVerificationHandler {
    private val log = logger()

    abstract val provider: Provider

    open val decoder: JwtDecoder
        get() = throw NotImplementedError()

    open fun verify(token: String): SocialResult {
        val jwt = try {
            decoder.decode(token)
        } catch (exception: JwtException) {
            val message = exception.message!!

            log.warn(message)

            if (message.contains("expired")) {
                throw StatusException(source, "access.expired", UNAUTHORIZED)
            }

            throw StatusException(source, "access.invalid", UNAUTHORIZED)
        }

        return mapper(jwt)
    }

    open fun mapper(jwt: Jwt): SocialResult {
        throw NotImplementedError()
    }
}
