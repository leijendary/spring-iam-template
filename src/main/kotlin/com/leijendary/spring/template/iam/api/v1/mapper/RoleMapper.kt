package com.leijendary.spring.template.iam.api.v1.mapper

import com.leijendary.spring.template.iam.api.v1.model.RoleRequest
import com.leijendary.spring.template.iam.api.v1.model.RoleResponse
import com.leijendary.spring.template.iam.entity.Role
import org.mapstruct.Mapper
import org.mapstruct.MappingTarget
import org.mapstruct.factory.Mappers.getMapper

@Mapper
interface RoleMapper {
    companion object {
        val INSTANCE: RoleMapper = getMapper(RoleMapper::class.java)
    }

    fun toResponse(role: Role): RoleResponse

    fun toEntity(roleRequest: RoleRequest): Role

    fun update(roleRequest: RoleRequest, @MappingTarget role: Role)
}
