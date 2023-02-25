package com.leijendary.spring.template.iam.core.config

import com.leijendary.spring.template.iam.core.config.properties.AuthProperties
import com.leijendary.spring.template.iam.core.security.JwtTools
import com.leijendary.spring.template.iam.core.security.JwtTools.TokenType.ACCESS_TOKEN
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.KeyUse
import com.nimbusds.jose.jwk.RSAKey
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JwksConfiguration(private val authProperties: AuthProperties, private val jwtTools: JwtTools) {
    @Bean
    fun jwkSet(): JWKSet {
        val publicKey = jwtTools.getPublicKey(ACCESS_TOKEN)
        val key = RSAKey.Builder(publicKey)
            .keyID(authProperties.keyId)
            .keyUse(KeyUse.SIGNATURE)
            .algorithm(JWSAlgorithm.RS256)
            .build()

        return JWKSet(key)
    }
}
