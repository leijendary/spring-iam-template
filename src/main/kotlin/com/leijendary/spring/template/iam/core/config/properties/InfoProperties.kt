package com.leijendary.spring.template.iam.core.config.properties

import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.License
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("info")
class InfoProperties {
    var app: App = App()
    var api: Api = Api()

    inner class App {
        lateinit var organization: String
        lateinit var group: String
        lateinit var name: String
        lateinit var description: String
        lateinit var version: String
    }

    inner class Api {
        lateinit var termsOfService: String
        var contact: Contact? = null
        var license: License? = null
        var extensions: Map<String, Any> = HashMap()
    }
}
