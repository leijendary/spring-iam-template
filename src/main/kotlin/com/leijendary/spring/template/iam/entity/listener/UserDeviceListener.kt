package com.leijendary.spring.template.iam.entity.listener

import com.leijendary.spring.template.iam.client.PushNotificationClient
import com.leijendary.spring.template.iam.core.util.SpringContext.Companion.getBean
import com.leijendary.spring.template.iam.entity.UserDevice
import jakarta.persistence.PrePersist
import jakarta.persistence.PreRemove

private val pushNotificationClient = getBean(PushNotificationClient::class)

class UserDeviceListener {
    @PrePersist
    fun beforeCreate(userDevice: UserDevice) {
        val platform = userDevice.platform
        val token = userDevice.token

        userDevice.endpoint = pushNotificationClient.createEndpoint(platform, token)
    }

    @PreRemove
    fun beforeRemove(userDevice: UserDevice) {
        userDevice.endpoint?.let {
            pushNotificationClient.deleteEndpoint(it)
        }
    }
}
