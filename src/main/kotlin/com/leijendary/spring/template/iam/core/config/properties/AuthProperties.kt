package com.leijendary.spring.template.iam.core.config.properties

import com.leijendary.spring.template.iam.core.util.RequestContext.now
import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration
import java.time.OffsetDateTime
import java.util.*

@ConfigurationProperties("auth")
class AuthProperties {
    var issuer: String = ""
    var keyId: String = ""
    var accessToken = TokenConfig()
    var refreshToken = TokenConfig()
    var system = System()

    inner class TokenConfig {
        var expiry: Duration = Duration.ofMinutes(5)
        var privateKey: String = ""
        var publicKey: String = ""

        fun computeExpiration(): OffsetDateTime = now.plus(expiry)
    }

    inner class System {
        var principal: String = ""
    }
}
