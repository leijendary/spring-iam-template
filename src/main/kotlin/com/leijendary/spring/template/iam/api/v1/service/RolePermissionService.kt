package com.leijendary.spring.template.iam.api.v1.service

import com.leijendary.spring.template.iam.api.v1.mapper.PermissionMapper
import com.leijendary.spring.template.iam.api.v1.model.PermissionResponse
import com.leijendary.spring.template.iam.api.v1.model.RolePermissionRequest
import com.leijendary.spring.template.iam.core.exception.ResourceNotFoundException
import com.leijendary.spring.template.iam.core.extension.transactional
import com.leijendary.spring.template.iam.entity.RolePermission
import com.leijendary.spring.template.iam.repository.PermissionRepository
import com.leijendary.spring.template.iam.repository.RolePermissionRepository
import com.leijendary.spring.template.iam.repository.RoleRepository
import org.springframework.data.jpa.domain.AbstractPersistable_.id
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class RolePermissionService(
    private val permissionRepository: PermissionRepository,
    private val rolePermissionRepository: RolePermissionRepository,
    private val roleRepository: RoleRepository
) {
    companion object {
        private val MAPPER = PermissionMapper.INSTANCE
        private val SOURCE = listOf("data", "Role", "id")
    }

    fun list(roleId: UUID): List<PermissionResponse> {
        val permissions = transactional(readOnly = true) {
            rolePermissionRepository
                .findAllByRoleId(roleId)
                .map { it.permission }
        }!!

        return permissions.map { MAPPER.toResponse(it!!) }
    }

    fun add(roleId: UUID, request: RolePermissionRequest): List<PermissionResponse> {
        val role = transactional(readOnly = true) {
            roleRepository
                .findByIdOrNull(roleId)
                ?: throw ResourceNotFoundException(SOURCE, id)
        }!!
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

        return permissions.map { MAPPER.toResponse(it) }
    }

    @Transactional
    fun remove(roleId: UUID, id: Long) {
        rolePermissionRepository.deleteAllByRoleIdAndPermissionId(roleId, id)
    }
}
