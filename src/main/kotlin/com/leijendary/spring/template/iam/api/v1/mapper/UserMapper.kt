package com.leijendary.spring.template.iam.api.v1.mapper

import com.leijendary.spring.template.iam.api.v1.model.RegisterEmailRequest
import com.leijendary.spring.template.iam.api.v1.model.RegisterPhoneRequest
import com.leijendary.spring.template.iam.api.v1.model.UserRequest
import com.leijendary.spring.template.iam.api.v1.model.UserResponse
import com.leijendary.spring.template.iam.entity.User
import com.leijendary.spring.template.iam.model.SocialResult
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.mapstruct.factory.Mappers.getMapper

@Mapper
interface UserMapper {
    companion object {
        val INSTANCE: UserMapper = getMapper(UserMapper::class.java)
    }

    fun toResponse(user: User): UserResponse

    fun toEntity(registerEmailRequest: RegisterEmailRequest): User

    fun toEntity(registerPhoneRequest: RegisterPhoneRequest): User

    fun toEntity(socialResult: SocialResult): User

    @Mapping(target = "account", ignore = true)
    @Mapping(target = "role", ignore = true)
    fun toEntity(userRequest: UserRequest): User

    @Mapping(target = "account", ignore = true)
    @Mapping(target = "role", ignore = true)
    fun update(userRequest: UserRequest, @MappingTarget user: User)
}
