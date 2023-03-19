package com.leijendary.spring.template.iam.entity.listener

import com.leijendary.spring.template.iam.core.util.SpringContext.Companion.getBean
import com.leijendary.spring.template.iam.entity.UserDevice
import com.leijendary.spring.template.iam.event.UserDeviceEvent
import jakarta.persistence.PostPersist
import jakarta.persistence.PostRemove

private val userDeviceEvent = getBean(UserDeviceEvent::class)

class UserDeviceListener {
    @PostPersist
    fun onCreate(userDevice: UserDevice) {
        userDeviceEvent.created(userDevice)
    }

    @PostRemove
    fun onRemove(userDevice: UserDevice) {
        userDeviceEvent.deleted(userDevice)
    }
}
