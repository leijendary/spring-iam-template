package com.leijendary.spring.template.iam.api.v1.service

import com.leijendary.spring.template.iam.api.v1.mapper.PermissionMapper
import com.leijendary.spring.template.iam.api.v1.model.PermissionResponse
import com.leijendary.spring.template.iam.api.v1.model.RolePermissionRequest
import com.leijendary.spring.template.iam.entity.RolePermission
import com.leijendary.spring.template.iam.repository.PermissionRepository
import com.leijendary.spring.template.iam.repository.RolePermissionRepository
import com.leijendary.spring.template.iam.repository.RoleRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class RolePermissionService(
    private val permissionRepository: PermissionRepository,
    private val rolePermissionRepository: RolePermissionRepository,
    private val roleRepository: RoleRepository
) {
    fun list(roleId: UUID): List<PermissionResponse> {
        val permissions = rolePermissionRepository
            .findAllByRoleId(roleId)
            .map { it.permission }

        return permissions.map(PermissionMapper.INSTANCE::toResponse)
    }

    fun add(roleId: UUID, request: RolePermissionRequest): List<PermissionResponse> {
        val role = roleRepository.findByIdOrThrow(roleId)
        val permissionIds = request.permissions.map { it.id }
        val permissions = permissionRepository.findAllById(permissionIds)
        val rolePermissions = permissions
            .map {
                RolePermission().apply {
                    this.role = role
                    this.permission = it
                }
            }

        rolePermissionRepository.saveAll(rolePermissions)

        return permissions.map(PermissionMapper.INSTANCE::toResponse)
    }

    @Transactional
    fun remove(roleId: UUID, id: Long) {
        rolePermissionRepository.deleteAllByRoleIdAndPermissionId(roleId, id)
    }
}
