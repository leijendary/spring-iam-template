package com.leijendary.spring.template.iam.api.v1.mapper

import com.leijendary.spring.template.iam.api.v1.model.ProfileRequest
import com.leijendary.spring.template.iam.api.v1.model.ProfileResponse
import com.leijendary.spring.template.iam.api.v1.model.UpdateEmailRequest
import com.leijendary.spring.template.iam.api.v1.model.UpdatePhoneRequest
import com.leijendary.spring.template.iam.core.util.BeanContainer.s3Storage
import com.leijendary.spring.template.iam.entity.User
import org.mapstruct.AfterMapping
import org.mapstruct.Mapper
import org.mapstruct.MappingTarget
import org.mapstruct.factory.Mappers.getMapper

@Mapper
interface ProfileMapper {
    companion object {
        val INSTANCE: ProfileMapper = getMapper(ProfileMapper::class.java)
    }

    fun toResponse(user: User): ProfileResponse

    fun update(profileRequest: ProfileRequest, @MappingTarget user: User)

    fun update(updateEmailRequest: UpdateEmailRequest, @MappingTarget user: User)

    fun update(updatePhoneRequest: UpdatePhoneRequest, @MappingTarget user: User)

    @AfterMapping
    fun setImage(@MappingTarget profileResponse: ProfileResponse) {
        val image = profileResponse.image

        if (image !== null && !image.startsWith("http")) {
            profileResponse.image = s3Storage.sign(image)
        }
    }
}
