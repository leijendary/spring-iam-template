package com.leijendary.spring.template.iam.core.storage

import com.leijendary.spring.template.iam.core.config.properties.AwsS3Properties
import com.leijendary.spring.template.iam.core.storage.S3Storage.Request.GET
import com.leijendary.spring.template.iam.core.storage.S3Storage.Request.PUT
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Service
import software.amazon.awssdk.core.ResponseInputStream
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.*
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.io.File

@Service
class S3Storage(
    private val awsS3Properties: AwsS3Properties,
    private val s3Client: S3Client,
    private val s3Presigner: S3Presigner
) {
    enum class Request {
        GET,
        PUT
    }

    fun sign(key: String, request: Request = GET) = when (request) {
        GET -> signGet(key)
        PUT -> signPut(key)
    }

    fun signGet(key: String): String {
        val request = GetObjectRequest
            .builder()
            .bucket(awsS3Properties.bucketName)
            .key(key)
            .build()
        val signRequest = GetObjectPresignRequest
            .builder()
            .getObjectRequest(request)
            .signatureDuration(awsS3Properties.signatureDuration)
            .build()

        return s3Presigner
            .presignGetObject(signRequest)
            .url()
            .toString()
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

        return s3Presigner
            .presignPutObject(signRequest)
            .url()
            .toString()
    }

    fun get(key: String): GetObjectResponse {
        return stream(key) { it.response() }
    }

    fun <T> stream(key: String, stream: (ResponseInputStream<GetObjectResponse>) -> T): T {
        val request = GetObjectRequest.builder()
            .bucket(awsS3Properties.bucketName)
            .key(key)
            .build()

        return s3Client.getObject(request).use(stream)
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
        stream(key) { inputStream ->
            servletResponse.run {
                contentType = inputStream.response().contentType()
                outputStream.use { inputStream::transferTo }
            }
        }
    }

    fun delete(key: String): DeleteObjectResponse {
        val request = DeleteObjectRequest.builder()
            .bucket(awsS3Properties.bucketName)
            .key(key)
            .build()

        return s3Client.deleteObject(request)
    }

    fun deleteAll(keys: List<String>): DeleteObjectsResponse {
        val ids = keys.map { ObjectIdentifier.builder().key(it).build() }
        val request = DeleteObjectsRequest.builder()
            .bucket(awsS3Properties.bucketName)
            .delete { it.objects(ids).build() }
            .build()

        return s3Client.deleteObjects(request)
    }
}
