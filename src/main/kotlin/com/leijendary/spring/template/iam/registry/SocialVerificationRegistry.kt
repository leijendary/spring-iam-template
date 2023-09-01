package com.leijendary.spring.template.iam.registry

import com.leijendary.spring.template.iam.entity.UserSocial
import com.leijendary.spring.template.iam.strategy.SocialVerificationStrategy
import org.springframework.stereotype.Component

@Component
class SocialVerificationRegistry(socialVerificationStrategies: List<SocialVerificationStrategy>) :
    Registry<UserSocial.Provider, SocialVerificationStrategy> {
    private val strategy = socialVerificationStrategies.associateBy { it.provider }

    override fun <R> using(type: UserSocial.Provider, function: SocialVerificationStrategy.() -> R?): R? {
        return strategy[type]?.run(function)
    }
}
