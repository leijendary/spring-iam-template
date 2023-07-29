package com.leijendary.spring.template.iam.repository

import com.leijendary.spring.template.iam.core.exception.ResourceNotFoundException
import com.leijendary.spring.template.iam.entity.UserAddress
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional
import java.util.*

private const val CACHE_NAME = "address"
private val source = listOf("data", "UserAddress", "id")

interface UserAddressRepository : JpaRepository<UserAddress, UUID> {
    @Transactional(readOnly = true)
    fun findByUserId(userId: UUID, pageable: Pageable): Page<UserAddress>

    @Transactional(readOnly = true)
    fun findAllByIdNotAndUserIdAndPrimary(id: UUID, userId: UUID, primary: Boolean = true): List<UserAddress>

    @Transactional(readOnly = true)
    fun findFirstByIdAndUserId(id: UUID, userId: UUID): UserAddress?

    @Transactional(readOnly = true)
    fun findFirstByIdAndUserIdOrThrow(id: UUID, userId: UUID): UserAddress {
        return findFirstByIdAndUserId(id, userId) ?: throw ResourceNotFoundException(source, id)
    }

    @Cacheable(value = [CACHE_NAME], key = "(#userId + ':' + #id)")
    @Transactional(readOnly = true)
    fun findFirstCachedByIdAndUserIdOrThrow(id: UUID, userId: UUID) = findFirstByIdAndUserIdOrThrow(id, userId)

    @CachePut(value = [CACHE_NAME], key = "(#result.user.id + ':' + #result.id)")
    fun saveAndCache(userAddress: UserAddress): UserAddress = save(userAddress)

    @CacheEvict(value = [CACHE_NAME], key = "(#result.user.id + ':' + #result.id)")
    fun saveAndEvict(userAddress: UserAddress): UserAddress = save(userAddress)

    @CacheEvict(value = [CACHE_NAME], key = "(#userAddress.user.id + ':' + #userAddress.id)")
    fun deleteAndEvict(userAddress: UserAddress) = delete(userAddress)
}
