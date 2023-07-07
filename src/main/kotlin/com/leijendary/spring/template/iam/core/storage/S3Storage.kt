package com.leijendary.spring.template.iam.core.storage

import com.leijendary.spring.template.iam.core.config.properties.AwsCloudFrontProperties
import com.leijendary.spring.template.iam.core.config.properties.AwsS3Properties
import com.leijendary.spring.template.iam.core.extension.rsaPrivateKey
import com.leijendary.spring.template.iam.core.storage.S3Storage.Request.GET
import com.leijendary.spring.template.iam.core.storage.S3Storage.Request.PUT
import com.leijendary.spring.template.iam.core.util.RequestContext.now
import jakarta.servlet.http.HttpServletResponse
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.springframework.stereotype.Service
import software.amazon.awssdk.core.ResponseInputStream
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.cloudfront.CloudFrontUtilities
import software.amazon.awssdk.services.cloudfront.model.CannedSignerRequest
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.*
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.io.File
import java.security.KeyFactory

private const val KEY_TEMP_SUFFIX = ".tmp"

@Service
class S3Storage(
    private val awsCloudFrontProperties: AwsCloudFrontProperties,
    private val awsS3Properties: AwsS3Properties,
    private val s3Client: S3Client,
    private val s3Presigner: S3Presigner
) {
    private val cloudFrontUtilities = CloudFrontUtilities.create()
    private val privateKey = KeyFactory.getInstance("RSA", BouncyCastleProvider())
        .rsaPrivateKey(awsCloudFrontProperties.privateKey)

    enum class Request {
        GET,
        PUT
    }

    fun signTemp(key: String): String {
        val tempKey = "$key$KEY_TEMP_SUFFIX"

        return signPut(tempKey)
    }

    fun moveTemp(key: String) {
        val tempKey = "$key$KEY_TEMP_SUFFIX"

        copy(tempKey, key)
        delete(tempKey)
    }

    fun sign(key: String, request: Request = GET) = when (request) {
        GET -> signGet(key)
        PUT -> signPut(key)
    }

    fun signGet(key: String): String {
        val resourceUrl = "${awsCloudFrontProperties.url}/$key"
        val expiry = now.plus(awsCloudFrontProperties.signatureDuration).toInstant()
        val signerRequest = CannedSignerRequest.builder()
            .keyPairId(awsCloudFrontProperties.publicKeyId)
            .resourceUrl(resourceUrl)
            .privateKey(privateKey)
            .expirationDate(expiry)
            .build()

        return cloudFrontUtilities.getSignedUrlWithCannedPolicy(signerRequest).url()
    }

    fun signPut(key: String): String {
        val request = PutObjectRequest
            .builder()
            .bucket(awsS3Properties.bucketName)
            .key(key)
            .build()
        val signRequest = PutObjectPresignRequest
            .builder()
            .putObjectRequest(request)
            .signatureDuration(awsS3Properties.signatureDuration)
            .build()

        return s3Presigner.presignPutObject(signRequest).url().toString()
    }

    fun copy(sourceKey: String, destinationKey: String): CopyObjectResponse {
        val request = CopyObjectRequest.builder()
            .sourceBucket(awsS3Properties.bucketName)
            .destinationBucket(awsS3Properties.bucketName)
            .sourceKey(sourceKey)
            .destinationKey(destinationKey)
            .build()

        return s3Client.copyObject(request)
    }

    fun get(key: String): GetObjectResponse {
        val s3Object = stream(key)

        return s3Object.response()
    }

    fun stream(key: String): ResponseInputStream<GetObjectResponse> {
        val request = GetObjectRequest.builder()
            .bucket(awsS3Properties.bucketName)
            .key(key)
            .build()

        return s3Client.getObject(request)
    }

    fun put(key: String, file: File): PutObjectResponse {
        val request = PutObjectRequest.builder()
            .bucket(awsS3Properties.bucketName)
            .key(key)
            .build()
        val body = RequestBody.fromFile(file)

        return s3Client.putObject(request, body)
    }

    fun render(key: String, servletResponse: HttpServletResponse) {
        val objectStream = stream(key)
        val s3Object = objectStream.response()
        val contentType = s3Object.contentType()
        val outputStream = servletResponse.outputStream

        servletResponse.contentType = contentType

        objectStream.transferTo(outputStream)
    }

    fun delete(key: String): DeleteObjectResponse {
        val request = DeleteObjectRequest.builder()
            .bucket(awsS3Properties.bucketName)
            .key(key)
            .build()

        return s3Client.deleteObject(request)
    }

    fun deleteAll(keys: List<String>): DeleteObjectsResponse {
        val ids = keys.map { key -> ObjectIdentifier.builder().key(key).build() }
        val request = DeleteObjectsRequest.builder()
            .bucket(awsS3Properties.bucketName)
            .delete {
                it.objects(ids).build()
            }
            .build()

        return s3Client.deleteObjects(request)
    }
}
