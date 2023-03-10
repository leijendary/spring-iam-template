package com.leijendary.spring.template.iam.api.v1.mapper

import com.leijendary.spring.template.iam.api.v1.model.VerificationCreateRequest
import com.leijendary.spring.template.iam.entity.Verification
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers.getMapper

@Mapper
interface VerificationMapper {
    companion object {
        val INSTANCE: VerificationMapper = getMapper(VerificationMapper::class.java)
    }

    fun toEntity(request: VerificationCreateRequest): Verification
}
