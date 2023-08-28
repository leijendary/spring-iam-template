package com.leijendary.spring.template.iam.core.config.properties

import com.leijendary.spring.template.iam.core.util.RequestContext.now
import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration
import java.time.OffsetDateTime

@ConfigurationProperties("auth")
class AuthProperties {
    lateinit var issuer: String
    lateinit var keyId: String
    var accessToken = TokenConfig()
    var refreshToken = TokenConfig()
    var system = System()
    var social = Social()

    inner class TokenConfig {
        var expiry: Duration = Duration.ofMinutes(5)
        lateinit var privateKey: String
        lateinit var publicKey: String

        fun computeExpiration(): OffsetDateTime = now.plus(expiry)
    }

    inner class System {
        lateinit var principal: String
    }

    inner class Social {
        var apple = SocialConfig()
        var facebook = FacebookConfig()
        var google = SocialConfig()
    }

    inner class SocialConfig {
        lateinit var clientId: String
        lateinit var issuer: String
        lateinit var jwkSetUri: String
    }

    inner class FacebookConfig {
        lateinit var url: String
        lateinit var profilePath: String
    }
}
