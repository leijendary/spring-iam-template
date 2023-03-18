package com.leijendary.spring.template.iam.strategy

import com.leijendary.spring.template.iam.client.FacebookClient
import com.leijendary.spring.template.iam.model.SocialProvider
import com.leijendary.spring.template.iam.model.SocialProvider.FACEBOOK
import com.leijendary.spring.template.iam.model.SocialResult
import org.springframework.stereotype.Component

@Component
class FacebookSocialVerificationStrategy(private val facebookClient: FacebookClient) : SocialVerificationStrategy() {
    override val provider: SocialProvider
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