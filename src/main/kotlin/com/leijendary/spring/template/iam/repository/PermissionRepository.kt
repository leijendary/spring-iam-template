package com.leijendary.spring.template.iam.repository

import com.leijendary.spring.template.iam.core.exception.ResourceNotFoundException
import com.leijendary.spring.template.iam.entity.Permission
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional

private const val CACHE_NAME = "permission"
private val source = listOf("data", "Permission", "id")

interface PermissionRepository : JpaRepository<Permission, Long> {
    @Transactional(readOnly = true)
    override fun findAll(pageable: Pageable): Page<Permission>

    @Transactional(readOnly = true)
    fun findAllByValueContainingIgnoreCase(value: String, pageable: Pageable): Page<Permission>

    @Transactional(readOnly = true)
    fun findByIdOrThrow(id: Long) = findByIdOrNull(id) ?: throw ResourceNotFoundException(source, id)

    @Cacheable(value = [CACHE_NAME], key = "#id")
    @Transactional(readOnly = true)
    fun findCachedByIdOrThrow(id: Long): Permission {
        return findByIdOrThrow(id)
    }

    @CachePut(value = [CACHE_NAME], key = "#result.id")
    fun saveAndCache(permission: Permission): Permission {
        return save(permission)
    }

    @CacheEvict(value = [CACHE_NAME], key = "#permission.id")
    fun deleteAndEvict(permission: Permission) {
        delete(permission)
    }
}
