package com.leijendary.spring.template.iam.api.v1.service

import com.leijendary.spring.template.iam.api.v1.mapper.RoleMapper
import com.leijendary.spring.template.iam.api.v1.model.RoleRequest
import com.leijendary.spring.template.iam.api.v1.model.RoleResponse
import com.leijendary.spring.template.iam.core.model.QueryRequest
import com.leijendary.spring.template.iam.repository.RoleRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class RoleService(private val roleRepository: RoleRepository) {
    fun list(request: QueryRequest, pageable: Pageable): Page<RoleResponse> {
        val query = request.query
        val page = if (query.isNullOrBlank()) {
            roleRepository.findAll(pageable)
        } else {
            roleRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query, pageable)
        }

        return page.map(RoleMapper.INSTANCE::toResponse)
    }

    fun create(request: RoleRequest): RoleResponse {
        val role = RoleMapper.INSTANCE.toEntity(request)
            .let(roleRepository::saveAndCache)

        return RoleMapper.INSTANCE.toResponse(role)
    }

    fun get(id: UUID): RoleResponse {
        val role = roleRepository.findCachedByIdOrThrow(id)

        return RoleMapper.INSTANCE.toResponse(role)
    }

    fun update(id: UUID, request: RoleRequest): RoleResponse {
        val role = roleRepository.findByIdOrThrow(id)

        RoleMapper.INSTANCE.update(request, role)

        roleRepository.saveAndCache(role)

        return RoleMapper.INSTANCE.toResponse(role)
    }

    @Transactional
    fun delete(id: UUID) {
        val role = roleRepository.findByIdOrThrow(id)

        roleRepository.deleteAndEvict(role)
    }
}
