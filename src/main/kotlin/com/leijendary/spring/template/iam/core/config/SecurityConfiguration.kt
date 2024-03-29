package com.leijendary.spring.template.iam.core.config

import com.leijendary.spring.template.iam.core.config.properties.AuthProperties
import com.leijendary.spring.template.iam.core.config.properties.AuthProperties.SocialConfig
import com.leijendary.spring.template.iam.core.security.JwtAudienceValidator
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtValidators.createDefaultWithIssuer
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder.withJwkSetUri

@Configuration
class SecurityConfiguration(private val authProperties: AuthProperties) {
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun appleJwtDecoder(): JwtDecoder {
        val config = authProperties.social.apple

        return createDecoder(config)
    }

    @Bean
    fun googleJwtDecoder(): JwtDecoder {
        val config = authProperties.social.google

        return createDecoder(config)
    }

    private fun createDecoder(config: SocialConfig): JwtDecoder {
        val issuer = config.issuer
        val defaultValidator = createDefaultWithIssuer(issuer)
        val clientId = config.clientId
        val audienceValidator = JwtAudienceValidator(clientId)
        val validator = DelegatingOAuth2TokenValidator(defaultValidator, audienceValidator)
        val jwkSetUri = config.jwkSetUri

        return withJwkSetUri(jwkSetUri)
            .build()
            .apply {
                setJwtValidator(validator)
            }
    }
}
