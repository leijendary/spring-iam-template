package com.leijendary.spring.template.iam.api.v1.mapper

import com.leijendary.spring.template.iam.api.v1.model.*
import com.leijendary.spring.template.iam.entity.User
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.mapstruct.factory.Mappers.getMapper

@Mapper
interface UserMapper {
    companion object {
        val INSTANCE: UserMapper = getMapper(UserMapper::class.java)
    }

    fun from(request: RegisterCustomerMobileRequest): User

    fun from(request: RegisterCustomerEmailRequest): User

    fun from(request: RegisterCustomerFullRequest): User

    fun toResponse(user: User): UserResponse

    @Mapping(target = "account", ignore = true)
    @Mapping(target = "role", ignore = true)
    fun toEntity(userRequest: UserRequest): User

    @Mapping(target = "account", ignore = true)
    @Mapping(target = "role", ignore = true)
    fun update(userRequest: UserRequest, @MappingTarget user: User)
}
