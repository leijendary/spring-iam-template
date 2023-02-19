package com.leijendary.spring.template.iam.api.v1.rest

import com.nimbusds.jose.jwk.JWKSet
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/.well-known/jwks.json")
@Tag(name = "JWK Set", description = "Get the public keys for verifying access tokens.")
class JwksRest(private val jwkSet: JWKSet) {
    @GetMapping
    fun keys(): Map<String, Any> = jwkSet.toJSONObject()
}
