package com.leijendary.spring.template.iam.strategy

import com.leijendary.spring.template.iam.entity.UserSocial.Provider
import com.leijendary.spring.template.iam.entity.UserSocial.Provider.GOOGLE
import com.leijendary.spring.template.iam.model.SocialResult
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.stereotype.Component

@Component
class GoogleSocialVerificationStrategy(private val googleJwtDecoder: JwtDecoder) : SocialVerificationStrategy() {
    override val provider: Provider
        get() = GOOGLE

    override val decoder: JwtDecoder
        get() = googleJwtDecoder

    override fun mapper(jwt: Jwt): SocialResult {
        return SocialResult(
            id = jwt.subject,
            firstName = jwt.getClaimAsString("given_name"),
            lastName = jwt.getClaimAsString("family_name"),
            email = jwt.getClaimAsString("email"),
            emailVerified = jwt.getClaimAsBoolean("email_verified"),
            picture = jwt.getClaimAsString("picture")
        )
    }
}
