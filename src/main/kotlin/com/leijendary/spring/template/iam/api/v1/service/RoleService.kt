package com.leijendary.spring.template.iam.api.v1.service

import com.leijendary.spring.template.iam.api.v1.mapper.RoleMapper
import com.leijendary.spring.template.iam.api.v1.model.RoleRequest
import com.leijendary.spring.template.iam.api.v1.model.RoleResponse
import com.leijendary.spring.template.iam.core.datasource.transactional
import com.leijendary.spring.template.iam.core.model.QueryRequest
import com.leijendary.spring.template.iam.repository.RoleRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class RoleService(private val roleRepository: RoleRepository) {
    companion object {
        private const val CACHE_NAME = "role:v1"
    }

    fun list(request: QueryRequest, pageable: Pageable): Page<RoleResponse> {
        val query = request.query
        val page = if (query.isNullOrBlank()) {
            roleRepository.findAll(pageable)
        } else {
            roleRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query, pageable)
        }

        return page.map { RoleMapper.INSTANCE.toResponse(it) }
    }

    @CachePut(value = [CACHE_NAME], key = "#result.id")
    fun create(request: RoleRequest): RoleResponse {
        val role = RoleMapper.INSTANCE
            .toEntity(request)
            .let { roleRepository.save(it) }

        return RoleMapper.INSTANCE.toResponse(role)
    }

    @Cacheable(value = [CACHE_NAME], key = "#id")
    fun get(id: UUID): RoleResponse {
        val role = roleRepository.findByIdOrThrow(id)

        return RoleMapper.INSTANCE.toResponse(role)
    }

    @CachePut(value = [CACHE_NAME], key = "#result.id")
    fun update(id: UUID, request: RoleRequest): RoleResponse {
        val role = transactional {
            roleRepository
                .findByIdOrThrow(id)
                .let {
                    RoleMapper.INSTANCE.update(request, it)

                    roleRepository.save(it)
                }
        }!!

        return RoleMapper.INSTANCE.toResponse(role)
    }

    @CacheEvict(value = [CACHE_NAME], key = "#id")
    @Transactional
    fun delete(id: UUID) {
        roleRepository
            .findByIdOrThrow(id)
            .also { roleRepository.delete(it) }
    }
}
