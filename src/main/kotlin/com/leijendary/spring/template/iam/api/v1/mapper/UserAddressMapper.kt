package com.leijendary.spring.template.iam.api.v1.mapper

import com.leijendary.spring.template.iam.api.v1.model.UserAddressRequest
import com.leijendary.spring.template.iam.api.v1.model.UserAddressResponse
import com.leijendary.spring.template.iam.entity.UserAddress
import org.mapstruct.Mapper
import org.mapstruct.MappingTarget
import org.mapstruct.factory.Mappers.getMapper

@Mapper
interface UserAddressMapper {
    companion object {
        val INSTANCE: UserAddressMapper = getMapper(UserAddressMapper::class.java)
    }

    fun toResponse(userAddress: UserAddress): UserAddressResponse

    fun toEntity(addressRequest: UserAddressRequest, country: String): UserAddress

    fun update(addressRequest: UserAddressRequest, country: String, @MappingTarget userAddress: UserAddress)
}
