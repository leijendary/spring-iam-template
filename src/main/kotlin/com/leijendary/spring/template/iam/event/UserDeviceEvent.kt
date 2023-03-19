package com.leijendary.spring.template.iam.event

import com.leijendary.spring.template.iam.api.v1.mapper.UserDeviceMapper
import com.leijendary.spring.template.iam.entity.UserDevice
import com.leijendary.spring.template.iam.message.UserDeviceMessageProducer
import org.springframework.stereotype.Component

@Component
class UserDeviceEvent(private val userDeviceMessageProducer: UserDeviceMessageProducer) {
    companion object {
        private val MAPPER = UserDeviceMapper.INSTANCE
    }

    fun created(userDevice: UserDevice) {
        val message = MAPPER.toMessage(userDevice)

        userDeviceMessageProducer.created(message)
    }

    fun deleted(userDevice: UserDevice) {
        val message = MAPPER.toMessage(userDevice)

        userDeviceMessageProducer.deleted(message)
    }
}