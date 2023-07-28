package com.leijendary.spring.template.iam.api.v1.service

import com.leijendary.spring.template.iam.api.v1.mapper.PermissionMapper
import com.leijendary.spring.template.iam.api.v1.model.PermissionRequest
import com.leijendary.spring.template.iam.api.v1.model.PermissionResponse
import com.leijendary.spring.template.iam.core.datasource.transactional
import com.leijendary.spring.template.iam.core.model.QueryRequest
import com.leijendary.spring.template.iam.repository.PermissionRepository
import com.leijendary.spring.template.iam.repository.RolePermissionRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PermissionService(
    private val rolePermissionRepository: RolePermissionRepository,
    private val permissionRepository: PermissionRepository
) {
    companion object {
        private const val CACHE_NAME = "permission:v1"
    }

    fun list(request: QueryRequest, pageable: Pageable): Page<PermissionResponse> {
        val query = request.query
        val page = if (query.isNullOrBlank()) {
            permissionRepository.findAll(pageable)
        } else {
            permissionRepository.findAllByValueContainingIgnoreCase(query, pageable)
        }

        return page.map { PermissionMapper.INSTANCE.toResponse(it) }
    }

    @CachePut(value = [CACHE_NAME], key = "#result.id")
    fun create(request: PermissionRequest): PermissionResponse {
        val permission = PermissionMapper.INSTANCE.toEntity(request)
            .let { permissionRepository.save(it) }

        return PermissionMapper.INSTANCE.toResponse(permission)
    }

    @Cacheable(value = [CACHE_NAME], key = "#id")
    fun get(id: Long): PermissionResponse {
        val permission = permissionRepository.findByIdOrThrow(id)

        return PermissionMapper.INSTANCE.toResponse(permission)
    }

    @CachePut(value = [CACHE_NAME], key = "#result.id")
    fun update(id: Long, request: PermissionRequest): PermissionResponse {
        val permission = transactional {
            permissionRepository
                .findByIdOrThrow(id)
                .let {
                    PermissionMapper.INSTANCE.update(request, it)

                    permissionRepository.save(it)
                }
        }!!

        return PermissionMapper.INSTANCE.toResponse(permission)
    }

    @CacheEvict(value = [CACHE_NAME], key = "#id")
    @Transactional
    fun delete(id: Long) {
        permissionRepository
            .findByIdOrThrow(id)
            .let {
                // Delete all role_permission first since they are connected to the role
                rolePermissionRepository.deleteAllByPermissionId(id)

                permissionRepository.delete(it)
            }
    }
}
