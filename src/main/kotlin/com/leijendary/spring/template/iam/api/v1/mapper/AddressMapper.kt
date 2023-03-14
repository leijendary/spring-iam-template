package com.leijendary.spring.template.iam.api.v1.mapper

import com.leijendary.spring.template.iam.api.v1.model.AddressRequest
import com.leijendary.spring.template.iam.api.v1.model.AddressResponse
import com.leijendary.spring.template.iam.entity.UserAddress
import org.mapstruct.Mapper
import org.mapstruct.MappingTarget
import org.mapstruct.factory.Mappers.getMapper

@Mapper
interface AddressMapper {
    companion object {
        val INSTANCE: AddressMapper = getMapper(AddressMapper::class.java)
    }

    fun toResponse(userAddress: UserAddress): AddressResponse

    fun toEntity(addressRequest: AddressRequest): UserAddress

    fun update(addressRequest: AddressRequest, @MappingTarget userAddress: UserAddress)
}
