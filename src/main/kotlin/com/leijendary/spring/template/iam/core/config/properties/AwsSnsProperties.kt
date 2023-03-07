package com.leijendary.spring.template.iam.core.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("spring.cloud.aws.sns")
class AwsSnsProperties {
    var platform = Platform()

    inner class Platform {
        var apple = Config()
        var firebase = Config()
    }

    inner class Config {
        var arn: String = ""
    }
}
