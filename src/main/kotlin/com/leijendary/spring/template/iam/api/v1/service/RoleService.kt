package com.leijendary.spring.template.iam.api.v1.service

import com.leijendary.spring.template.iam.api.v1.mapper.RoleMapper
import com.leijendary.spring.template.iam.api.v1.model.RoleRequest
import com.leijendary.spring.template.iam.api.v1.model.RoleResponse
import com.leijendary.spring.template.iam.core.exception.ResourceNotFoundException
import com.leijendary.spring.template.iam.core.extension.transactional
import com.leijendary.spring.template.iam.core.model.QueryRequest
import com.leijendary.spring.template.iam.repository.RoleRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

@Service
class RoleService(private val roleRepository: RoleRepository) {
    companion object {
        private const val CACHE_NAME = "role:v1"
        private val MAPPER = RoleMapper.INSTANCE
        private val SOURCE = listOf("data", "Role", "id")
    }

    fun list(request: QueryRequest, pageable: Pageable): Page<RoleResponse> {
        val query = request.query
        val page = transactional(readOnly = true) {
            if (query.isNullOrBlank()) {
                roleRepository.findAll(pageable)
            } else {
                roleRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query, pageable)
            }
        }

        return page.map { MAPPER.toResponse(it) }
    }

    @CachePut(value = [CACHE_NAME], key = "#result.id")
    fun create(request: RoleRequest): RoleResponse {
        val sampleTable = transactional {
            MAPPER
                .toEntity(request)
                .let {
                    roleRepository.save(it)
                }
        }

        return MAPPER.toResponse(sampleTable)
    }

    @Cacheable(value = [CACHE_NAME], key = "#id")
    fun get(id: UUID): RoleResponse {
        val role = transactional(readOnly = true) {
            roleRepository
                .findById(id)
                .orElseThrow { ResourceNotFoundException(SOURCE, id) }
        }

        return MAPPER.toResponse(role)
    }

    @CachePut(value = [CACHE_NAME], key = "#result.id")
    fun update(id: UUID, request: RoleRequest): RoleResponse {
        val role = transactional {
            roleRepository
                .findById(id)
                .orElseThrow { ResourceNotFoundException(SOURCE, id) }
                .let {
                    MAPPER.update(request, it)

                    roleRepository.save(it)
                }
        }

        return MAPPER.toResponse(role)
    }

    @CacheEvict(value = [CACHE_NAME], key = "#id")
    fun delete(id: UUID) {
        transactional {
            roleRepository
                .findById(id)
                .orElseThrow { ResourceNotFoundException(SOURCE, id) }
                .let {
                    roleRepository.delete(it)

                    it
                }
        }
    }
}
