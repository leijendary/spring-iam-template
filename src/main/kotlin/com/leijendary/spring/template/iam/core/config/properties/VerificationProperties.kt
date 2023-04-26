package com.leijendary.spring.template.iam.core.config.properties

import com.leijendary.spring.template.iam.core.util.RequestContext.now
import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration
import java.time.OffsetDateTime

@ConfigurationProperties(prefix = "verification")
class VerificationProperties {
    var expiry: Duration = Duration.ofHours(2)
    var timeout: Duration = Duration.ofSeconds(60)
    var register = Config()
    var password = Password()
    var email = Config()

    open inner class Config {
        lateinit var template: String
    }

    inner class Password {
        var nominate = PasswordNominate()
        var reset = Config()
    }

    inner class PasswordNominate : Config() {
        lateinit var url: String
    }

    fun computeExpiration(): OffsetDateTime = now.plus(expiry)

    fun computeTimeout(): OffsetDateTime = now.minusSeconds(timeout.toSeconds())
}
