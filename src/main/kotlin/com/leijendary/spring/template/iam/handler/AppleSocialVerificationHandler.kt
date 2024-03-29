package com.leijendary.spring.template.iam.handler

import com.leijendary.spring.template.iam.entity.UserSocial.Provider
import com.leijendary.spring.template.iam.entity.UserSocial.Provider.APPLE
import com.leijendary.spring.template.iam.model.SocialResult
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.stereotype.Component

@Component
class AppleSocialVerificationHandler(private val appleJwtDecoder: JwtDecoder) : SocialVerificationHandler() {
    override val provider: Provider
        get() = APPLE

    override val decoder: JwtDecoder
        get() = appleJwtDecoder

    override fun mapper(jwt: Jwt): SocialResult {
        return SocialResult(
            id = jwt.subject,
            email = jwt.getClaimAsString("email"),
            emailVerified = jwt.getClaimAsBoolean("email_verified")
        )
    }
}
