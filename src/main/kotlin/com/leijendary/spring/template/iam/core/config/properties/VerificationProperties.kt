package com.leijendary.spring.template.iam.core.config.properties

import com.leijendary.spring.template.iam.core.util.RequestContext.now
import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration
import java.time.OffsetDateTime

@ConfigurationProperties(prefix = "verification")
class VerificationProperties {
    var expiry: Duration = Duration.ofHours(2)
    var register = Config()
    var password = Password()
    var email = Config()

    open inner class Config {
        var subject: String? = null
    }

    inner class Password {
        var nominate = PasswordNominate()
        var reset = Config()
    }

    inner class PasswordNominate : Config() {
        var url: String? = null
    }

    fun computeExpiration(): OffsetDateTime = now.plus(expiry)
}
