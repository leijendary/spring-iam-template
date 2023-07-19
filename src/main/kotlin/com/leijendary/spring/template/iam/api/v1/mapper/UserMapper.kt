package com.leijendary.spring.template.iam.api.v1.mapper

import com.leijendary.spring.template.iam.api.v1.model.RegisterEmailRequest
import com.leijendary.spring.template.iam.api.v1.model.RegisterPhoneRequest
import com.leijendary.spring.template.iam.api.v1.model.UserRequest
import com.leijendary.spring.template.iam.api.v1.model.UserResponse
import com.leijendary.spring.template.iam.core.util.BeanContainer.s3Storage
import com.leijendary.spring.template.iam.entity.User
import com.leijendary.spring.template.iam.model.SocialResult
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

    @Mapping(target = "image", ignore = true)
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

    @AfterMapping
    fun setImage(user: User, @MappingTarget userResponse: UserResponse) {
        val image = user.image

        if (image != null && !image.startsWith("http")) {
            userResponse.image = image.let { s3Storage.sign(it) }
        }
    }
}
