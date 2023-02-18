package com.leijendary.spring.template.iam.core.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("auth")
class AuthProperties {
    var system: System = System()

    class System {
        var principal: String = ""
    }
}
