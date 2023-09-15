package com.leijendary.spring.template.iam.registry

import com.leijendary.spring.template.iam.entity.UserSocial
import com.leijendary.spring.template.iam.handler.SocialVerificationHandler
import org.springframework.stereotype.Component

@Component
class SocialVerificationRegistry(socialVerificationStrategies: List<SocialVerificationHandler>) :
    Registry<UserSocial.Provider, SocialVerificationHandler> {
    private val strategy = socialVerificationStrategies.associateBy { it.provider }

    override fun <R> using(type: UserSocial.Provider, function: SocialVerificationHandler.() -> R?): R? {
        return strategy[type]?.run(function)
    }
}
