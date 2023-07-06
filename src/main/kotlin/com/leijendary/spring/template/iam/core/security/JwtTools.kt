package com.leijendary.spring.template.iam.core.security

import com.leijendary.spring.template.iam.core.config.properties.AuthProperties
import com.leijendary.spring.template.iam.core.extension.rsaPrivateKey
import com.leijendary.spring.template.iam.core.extension.rsaPublicKey
import com.leijendary.spring.template.iam.core.mapper.DateMapper
import com.leijendary.spring.template.iam.core.model.JwtSet
import com.leijendary.spring.template.iam.core.model.ParsedJwt
import com.leijendary.spring.template.iam.core.model.Token
import com.leijendary.spring.template.iam.core.security.JwtTools.TokenType.ACCESS_TOKEN
import com.leijendary.spring.template.iam.core.security.JwtTools.TokenType.REFRESH_TOKEN
import com.leijendary.spring.template.iam.core.util.RequestContext.now
import com.nimbusds.jose.JOSEObjectType
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.crypto.RSASSAVerifier
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import org.springframework.core.task.AsyncTaskExecutor
import org.springframework.stereotype.Component
import java.security.KeyFactory
import java.util.*
import java.util.concurrent.Callable

private const val CLAIM_SCOPE = "scope"
private const val CLAIM_ATI = "ati"
private const val CLAIM_ISSUE_TIME = "iat"
private val keyFactory = KeyFactory.getInstance("RSA")

@Component
class JwtTools(private val asyncTaskExecutor: AsyncTaskExecutor, private val authProperties: AuthProperties) {
    val publicKey = mapOf(
        ACCESS_TOKEN to keyFactory.rsaPublicKey(authProperties.accessToken.publicKey),
        REFRESH_TOKEN to keyFactory.rsaPublicKey(authProperties.refreshToken.publicKey),
    )

    private val privateKey = mapOf(
        ACCESS_TOKEN to keyFactory.rsaPrivateKey(authProperties.accessToken.privateKey),
        REFRESH_TOKEN to keyFactory.rsaPrivateKey(authProperties.refreshToken.privateKey),
    )

    enum class TokenType {
        ACCESS_TOKEN,
        REFRESH_TOKEN
    }

    fun create(subject: String, audience: String, scopes: Set<String>): JwtSet {
        val accessId = UUID.randomUUID()
        val accessToken = asyncTaskExecutor.submit(Callable {
            create(accessId, ACCESS_TOKEN, subject, audience, scopes)
        })
        val refreshToken = asyncTaskExecutor.submit(Callable {
            val refreshId = UUID.randomUUID()

            create(refreshId, REFRESH_TOKEN, subject, audience, scopes, accessId.toString())
        })

        return JwtSet(accessToken.get(), refreshToken.get())
    }

    fun create(
        id: UUID,
        type: TokenType,
        subject: String,
        audience: String,
        scopes: Set<String>,
        ati: String? = null
    ): Token {
        val expiration = when (type) {
            ACCESS_TOKEN -> authProperties.accessToken.computeExpiration()
            REFRESH_TOKEN -> authProperties.refreshToken.computeExpiration()
        }
        val header = JWSHeader.Builder(JWSAlgorithm.RS256)
            .type(JOSEObjectType.JWT)
            .keyID(authProperties.keyId)
            .build()
        val expirationMilli = DateMapper.INSTANCE.toEpochMilli(expiration)
        val expirationTime = Date(expirationMilli)
        val claims = JWTClaimsSet.Builder()
            .jwtID(id.toString())
            .issuer(authProperties.issuer)
            .subject(subject)
            .audience(audience)
            .expirationTime(expirationTime)
            .claim(CLAIM_ISSUE_TIME, now.toEpochSecond())

        if (scopes.isNotEmpty()) {
            val scope = scopes.joinToString(" ")

            claims.claim(CLAIM_SCOPE, scope)
        }

        ati?.let {
            claims.claim(CLAIM_ATI, it)
        }

        val privateKey = privateKey[type]!!
        val signer = RSASSASigner(privateKey)
        val signed = SignedJWT(header, claims.build())
        signed.sign(signer)

        val token = signed.serialize()

        return Token(id, token, expiration)
    }

    fun parse(token: String): ParsedJwt {
        val signed = SignedJWT.parse(token)
        val claimsSet = signed.jwtClaimsSet
        val id = claimsSet.jwtid
        val issuer = claimsSet.issuer
        val subject = claimsSet.subject
        val audience = claimsSet.audience
        val scopes = claimsSet.claims[CLAIM_SCOPE]
            ?.let { it as String }
            ?.split(" ")
            ?.toSet()
            ?: emptySet()
        val expirationTime = claimsSet
            .expirationTime
            .let { DateMapper.INSTANCE.toOffsetDateTime(it.time) }
        val issueTime = claimsSet
            .issueTime
            .let { DateMapper.INSTANCE.toOffsetDateTime(it.time) }
        val accessTokenId = claimsSet.getStringClaim(CLAIM_ATI)
        val type = if (accessTokenId == null) ACCESS_TOKEN else REFRESH_TOKEN
        val publicKey = publicKey[type]!!
        val verifier = RSASSAVerifier(publicKey)
        val isVerified = signed.verify(verifier)

        return ParsedJwt(
            id = id,
            issuer = issuer,
            subject = subject,
            audience = audience,
            scopes = scopes,
            expirationTime = expirationTime,
            issueTime = issueTime,
            accessTokenId = accessTokenId,
            isVerified = isVerified
        )
    }

    fun getPublicKey(type: TokenType) = publicKey[type]!!
}
