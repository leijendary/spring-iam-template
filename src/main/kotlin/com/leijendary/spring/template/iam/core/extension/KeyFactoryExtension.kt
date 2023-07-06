package com.leijendary.spring.template.iam.core.extension

import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*

private val decoder = Base64.getDecoder()

fun KeyFactory.rsaPrivateKey(privateKey: String): RSAPrivateKey {
    val base64 = decoder.decode(privateKey)
    val keySpec = PKCS8EncodedKeySpec(base64)

    return generatePrivate(keySpec) as RSAPrivateKey
}

fun KeyFactory.rsaPublicKey(publicKey: String): RSAPublicKey {
    val base64 = decoder.decode(publicKey)
    val keySpec = X509EncodedKeySpec(base64)

    return generatePublic(keySpec) as RSAPublicKey
}
