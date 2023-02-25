package com.leijendary.spring.template.iam.core.security

import com.leijendary.spring.template.iam.core.config.properties.AuthProperties
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
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.springframework.stereotype.Component
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*

private const val CLAIM_SCOPE = "scope"
private const val CLAIM_ATI = "ati"
private const val CLAIM_ISSUE_TIME = "iat"
private val keyFactory = KeyFactory.getInstance("RSA")

@Component
class JwtTools(private val authProperties: AuthProperties) {
    private val privateKey = mapOf(
        ACCESS_TOKEN to rsaPrivateKey(authProperties.accessToken.privateKey),
        REFRESH_TOKEN to rsaPrivateKey(authProperties.refreshToken.privateKey),
    )

    val publicKey = mapOf(
        ACCESS_TOKEN to rsaPublicKey(authProperties.accessToken.publicKey),
        REFRESH_TOKEN to rsaPublicKey(authProperties.refreshToken.publicKey),
    )

    companion object {
        private val MAPPER = DateMapper.INSTANCE
    }

    enum class TokenType {
        ACCESS_TOKEN,
        REFRESH_TOKEN
    }

    fun create(subject: String, audience: String, scopes: Set<String>): JwtSet = runBlocking {
        val accessId = UUID.randomUUID()
        val accessToken = async {
            create(accessId, ACCESS_TOKEN, subject, audience, scopes)
        }
        val refreshToken = async {
            val refreshId = UUID.randomUUID()

            create(refreshId, REFRESH_TOKEN, subject, audience, scopes, accessId.toString())
        }

        JwtSet(accessToken.await(), refreshToken.await())
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
        val expirationMilli = MAPPER.toEpochMilli(expiration)
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
        val scopes = claimsSet
            .getStringClaim(CLAIM_SCOPE)
            .split(" ")
            .toSet()
        val expirationTime = claimsSet
            .expirationTime
            .let { MAPPER.toOffsetDateTime(it.time) }
        val issueTime = claimsSet
            .issueTime
            .let { MAPPER.toOffsetDateTime(it.time) }
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

    private fun rsaPrivateKey(privateKey: String): RSAPrivateKey {
        return privateKey
            .replace("\\n", "")
            .let {
                val base64 = Base64.getDecoder().decode(it)
                val keySpec = PKCS8EncodedKeySpec(base64)

                keyFactory.generatePrivate(keySpec) as RSAPrivateKey
            }
    }

    private fun rsaPublicKey(publicKey: String): RSAPublicKey {
        return publicKey
            .replace("\\n", "")
            .let {
                val base64 = Base64.getDecoder().decode(it)
                val keySpec = X509EncodedKeySpec(base64)

                keyFactory.generatePublic(keySpec) as RSAPublicKey
            }
    }
}
