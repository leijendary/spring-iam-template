package com.leijendary.spring.template.iam.api.v1.mapper

import com.leijendary.spring.template.iam.api.v1.model.RegisterEmailRequest
import com.leijendary.spring.template.iam.api.v1.model.RegisterPhoneRequest
import com.leijendary.spring.template.iam.api.v1.model.UserRequest
import com.leijendary.spring.template.iam.api.v1.model.UserResponse
import com.leijendary.spring.template.iam.core.util.BeanContainer.s3Storage
import com.leijendary.spring.template.iam.entity.User
import com.leijendary.spring.template.iam.model.SocialResult
import com.leijendary.spring.template.iam.model.UserEmailMessage
import com.leijendary.spring.template.iam.model.UserMessage
import com.leijendary.spring.template.iam.model.UserPhoneMessage
import org.mapstruct.AfterMapping
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

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "image", source = "picture")
    fun toEntity(socialResult: SocialResult): User

    @Mapping(target = "account", ignore = true)
    @Mapping(target = "role", ignore = true)
    fun toEntity(userRequest: UserRequest): User

    @Mapping(target = "account", ignore = true)
    @Mapping(target = "role", ignore = true)
    fun update(userRequest: UserRequest, @MappingTarget user: User)

    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "roleId", source = "role.id")
    fun toMessage(user: User): UserMessage

    fun toEmailMessage(user: User): UserEmailMessage

    fun toPhoneMessage(user: User): UserPhoneMessage

    @AfterMapping
    fun setImage(@MappingTarget userResponse: UserResponse) {
        val image = userResponse.image

        if (image != null && !image.startsWith("http")) {
            userResponse.image = image.let(s3Storage::sign)
        }
    }
}
