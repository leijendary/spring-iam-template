package com.leijendary.spring.template.iam.core.config.properties

import com.leijendary.spring.template.iam.core.util.RequestContext.now
import org.springframework.boot.context.properties.ConfigurationProperties
import java.net.URL
import java.time.Duration
import java.time.OffsetDateTime

@ConfigurationProperties(prefix = "verification")
class VerificationProperties {
    var expiry: Duration = Duration.ofHours(2)
    var register = Config()
    var password = Password()

    inner class Config {
        var subject: String? = null
        var url: URL? = null
    }

    inner class Password {
        var change = Config()
        var reset = Config()
    }

    fun computeExpiration(): OffsetDateTime = now.plus(expiry)
}
