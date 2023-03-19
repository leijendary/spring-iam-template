package com.leijendary.spring.template.iam.api.v1.mapper

import com.leijendary.spring.template.iam.api.v1.model.UserDeviceRequest
import com.leijendary.spring.template.iam.entity.UserDevice
import com.leijendary.spring.template.iam.model.UserDeviceMessage
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.factory.Mappers.getMapper

@Mapper
interface UserDeviceMapper {
    companion object {
        val INSTANCE: UserDeviceMapper = getMapper(UserDeviceMapper::class.java)
    }

    fun toEntity(userDeviceRequest: UserDeviceRequest): UserDevice

    @Mapping(target = "userId", source = "user.id")
    fun toMessage(userDevice: UserDevice): UserDeviceMessage
}
