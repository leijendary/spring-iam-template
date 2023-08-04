package com.leijendary.spring.template.iam.api.v1.mapper

import com.leijendary.spring.template.iam.api.v1.model.UserProviderRequest
import com.leijendary.spring.template.iam.api.v1.model.UserProviderResponse
import com.leijendary.spring.template.iam.entity.UserProvider
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

@Mapper
interface UserProviderMapper {
    companion object {
        val INSTANCE: UserProviderMapper = Mappers.getMapper(UserProviderMapper::class.java)
    }

    fun toEntity(userProviderRequest: UserProviderRequest): UserProvider

    fun toResponse(userProvider: UserProvider): UserProviderResponse
}
