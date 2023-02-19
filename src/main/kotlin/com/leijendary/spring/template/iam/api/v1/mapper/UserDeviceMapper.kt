package com.leijendary.spring.template.iam.api.v1.mapper

import com.leijendary.spring.template.iam.api.v1.model.TokenRequest
import com.leijendary.spring.template.iam.entity.UserDevice
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.factory.Mappers.getMapper

@Mapper
interface UserDeviceMapper {
    companion object {
        val INSTANCE: UserDeviceMapper = getMapper(UserDeviceMapper::class.java)
    }

    @Mapping(source = "deviceId", target = "token")
    fun from(tokenRequest: TokenRequest): UserDevice
}
