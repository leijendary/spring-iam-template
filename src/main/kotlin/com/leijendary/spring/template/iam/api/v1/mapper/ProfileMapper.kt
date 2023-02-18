package com.leijendary.spring.template.iam.api.v1.mapper

import com.leijendary.spring.template.iam.api.v1.model.ProfileRequest
import com.leijendary.spring.template.iam.api.v1.model.ProfileResponse
import com.leijendary.spring.template.iam.entity.User
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
}
