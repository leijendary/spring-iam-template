package com.leijendary.spring.template.iam.core.config.properties

import com.leijendary.spring.template.iam.core.util.RequestContext.now
import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration
import java.time.OffsetDateTime

@ConfigurationProperties(prefix = "verification")
class VerificationProperties {
    var expiry: Duration = Duration.ofHours(2)
    var email = Config()
    var password = Password()
    var register = Config()

    inner class Config {
        var subject: String? = null
    }

    inner class Password {
        var change = Config()
        var reset = Config()
    }

    fun computeExpiration(): OffsetDateTime = now.plus(expiry)
}
