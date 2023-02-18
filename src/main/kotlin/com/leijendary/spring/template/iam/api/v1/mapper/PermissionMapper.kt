package com.leijendary.spring.template.iam.api.v1.mapper

import com.leijendary.spring.template.iam.api.v1.model.PermissionRequest
import com.leijendary.spring.template.iam.api.v1.model.PermissionResponse
import com.leijendary.spring.template.iam.entity.Permission
import org.mapstruct.Mapper
import org.mapstruct.MappingTarget
import org.mapstruct.factory.Mappers.getMapper

@Mapper
interface PermissionMapper {
    companion object {
        val INSTANCE: PermissionMapper = getMapper(PermissionMapper::class.java)
    }

    fun toResponse(permission: Permission): PermissionResponse

    fun toEntity(permissionRequest: PermissionRequest): Permission

    fun update(permissionRequest: PermissionRequest, @MappingTarget permission: Permission): Permission
}
