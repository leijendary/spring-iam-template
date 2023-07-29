package com.leijendary.spring.template.iam.api.v1.service

import com.leijendary.spring.template.iam.api.v1.mapper.PermissionMapper
import com.leijendary.spring.template.iam.api.v1.model.PermissionRequest
import com.leijendary.spring.template.iam.api.v1.model.PermissionResponse
import com.leijendary.spring.template.iam.core.model.QueryRequest
import com.leijendary.spring.template.iam.repository.PermissionRepository
import com.leijendary.spring.template.iam.repository.RolePermissionRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PermissionService(
    private val rolePermissionRepository: RolePermissionRepository,
    private val permissionRepository: PermissionRepository
) {
    fun list(request: QueryRequest, pageable: Pageable): Page<PermissionResponse> {
        val query = request.query
        val page = if (query.isNullOrBlank()) {
            permissionRepository.findAll(pageable)
        } else {
            permissionRepository.findAllByValueContainingIgnoreCase(query, pageable)
        }

        return page.map(PermissionMapper.INSTANCE::toResponse)
    }

    fun create(request: PermissionRequest): PermissionResponse {
        val permission = PermissionMapper.INSTANCE.toEntity(request)
            .let(permissionRepository::saveAndCache)

        return PermissionMapper.INSTANCE.toResponse(permission)
    }

    fun get(id: Long): PermissionResponse {
        val permission = permissionRepository.findCachedByIdOrThrow(id)

        return PermissionMapper.INSTANCE.toResponse(permission)
    }

    fun update(id: Long, request: PermissionRequest): PermissionResponse {
        val permission = permissionRepository.findByIdOrThrow(id)

        PermissionMapper.INSTANCE.update(request, permission)

        permissionRepository.saveAndCache(permission)

        return PermissionMapper.INSTANCE.toResponse(permission)
    }

    @Transactional
    fun delete(id: Long) {
        permissionRepository
            .findByIdOrThrow(id)
            .let {
                // Delete all role_permission first since they are connected to the role
                rolePermissionRepository.deleteAllByPermissionId(id)
                permissionRepository.deleteAndEvict(it)
            }
    }
}
