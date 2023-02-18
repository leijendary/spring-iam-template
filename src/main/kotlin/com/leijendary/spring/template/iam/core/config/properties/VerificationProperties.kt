package com.leijendary.spring.template.iam.core.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "verification")
class VerificationProperties {
    // In minutes
    var expiry: Duration = Duration.ofMinutes(5)
    var register = Config()
    var resetPassword = Config()

    class Config {
        var subject: String? = null
        var url: String? = null
    }
}
