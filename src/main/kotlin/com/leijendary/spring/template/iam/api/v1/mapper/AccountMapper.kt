package com.leijendary.spring.template.iam.api.v1.mapper

import com.leijendary.spring.template.iam.api.v1.model.AccountResponse
import com.leijendary.spring.template.iam.entity.Account
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

@Mapper
interface AccountMapper {
    companion object {
        val INSTANCE: AccountMapper = Mappers.getMapper(AccountMapper::class.java)
    }

    fun toResponse(account: Account): AccountResponse
}
