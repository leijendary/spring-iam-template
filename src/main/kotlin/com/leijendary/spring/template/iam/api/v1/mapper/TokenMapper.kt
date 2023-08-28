package com.leijendary.spring.template.iam.api.v1.mapper

import com.leijendary.spring.template.iam.api.v1.model.TokenResponse
import com.leijendary.spring.template.iam.entity.Auth
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.factory.Mappers.getMapper

@Mapper(uses = [ProfileMapper::class])
interface TokenMapper {
    companion object {
        val INSTANCE: TokenMapper = getMapper(TokenMapper::class.java)
    }

    @Mapping(target = "profile", source = "auth.user")
    fun toResponse(auth: Auth): TokenResponse
}
