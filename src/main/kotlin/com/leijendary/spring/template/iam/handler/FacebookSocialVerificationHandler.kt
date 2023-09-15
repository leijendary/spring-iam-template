package com.leijendary.spring.template.iam.handler

import com.leijendary.spring.template.iam.client.FacebookClient
import com.leijendary.spring.template.iam.entity.UserSocial.Provider
import com.leijendary.spring.template.iam.entity.UserSocial.Provider.FACEBOOK
import com.leijendary.spring.template.iam.model.SocialResult
import org.springframework.stereotype.Component

@Component
class FacebookSocialVerificationHandler(private val facebookClient: FacebookClient) : SocialVerificationHandler() {
    override val provider: Provider
        get() = FACEBOOK

    override fun verify(token: String): SocialResult {
        val response = facebookClient.profile(token)

        return SocialResult(
            id = response.id,
            firstName = response.firstName,
            lastName = response.lastName,
            email = response.email,
            emailVerified = true,
            picture = response.getPicture()
        )
    }
}
