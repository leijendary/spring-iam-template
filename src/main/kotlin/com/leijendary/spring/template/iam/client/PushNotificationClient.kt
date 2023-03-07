package com.leijendary.spring.template.iam.client

import com.leijendary.spring.template.iam.core.config.properties.AwsSnsProperties
import com.leijendary.spring.template.iam.model.DevicePlatform
import com.leijendary.spring.template.iam.model.DevicePlatform.ANDROID
import com.leijendary.spring.template.iam.model.DevicePlatform.IOS
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.sns.SnsClient

@Component
class PushNotificationClient(private val awsSnsProperties: AwsSnsProperties, private val snsClient: SnsClient) {
    fun createEndpoint(platform: String, token: String): String? {
        val arn = when (DevicePlatform.from(platform)) {
            IOS -> awsSnsProperties.platform.apple.arn
            ANDROID -> awsSnsProperties.platform.firebase.arn
            else -> return null
        }
        val response = snsClient.createPlatformEndpoint {
            it.platformApplicationArn(arn)
            it.token(token)
        }

        return response.endpointArn()
    }

    fun deleteEndpoint(endpoint: String) {
        snsClient.deleteEndpoint {
            it.endpointArn(endpoint)
        }
    }
}
