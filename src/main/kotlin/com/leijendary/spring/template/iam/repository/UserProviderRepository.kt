package com.leijendary.spring.template.iam.repository

import com.leijendary.spring.template.iam.core.exception.ResourceNotFoundException
import com.leijendary.spring.template.iam.entity.UserProvider
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional
import java.util.*

private const val CACHE_NAME = "userProviderReference"
private val sourceProvider = listOf("data", "UserProviderReference", "provider")

interface UserProviderRepository : JpaRepository<UserProvider, Long> {
    @Transactional(readOnly = true)
    fun findByUserId(userId: UUID, pageable: Pageable): Page<UserProvider>

    @Transactional(readOnly = true)
    fun findFirstByUserIdAndProviderAndType(userId: UUID, provider: String, type: String): UserProvider?

    @Transactional(readOnly = true)
    fun findFirstByUserIdAndProviderAndTypeOrThrow(userId: UUID, provider: String, type: String): UserProvider {
        return findFirstByUserIdAndProviderAndType(userId, provider, type)
            ?: throw ResourceNotFoundException(sourceProvider, "$provider:$type")
    }

    @Cacheable(value = [CACHE_NAME], key = "(#userId + ':' + #provider + ':' + #type)")
    @Transactional(readOnly = true)
    fun findFirstCachedByUserIdAndProviderAndTypeOrThrow(userId: UUID, provider: String, type: String): UserProvider {
        return findFirstByUserIdAndProviderAndTypeOrThrow(userId, provider, type)
    }

    @CachePut(value = [CACHE_NAME], key = "(#result.user.id + ':' + #result.provider + ':' + #result.type)")
    fun saveAndCache(userProvider: UserProvider): UserProvider {
        return save(userProvider)
    }
}
