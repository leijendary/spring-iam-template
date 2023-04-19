package com.leijendary.spring.template.iam.api.v1.service

import com.leijendary.spring.template.iam.api.v1.mapper.AddressMapper
import com.leijendary.spring.template.iam.api.v1.model.AddressRequest
import com.leijendary.spring.template.iam.api.v1.model.AddressResponse
import com.leijendary.spring.template.iam.core.datasource.transactional
import com.leijendary.spring.template.iam.repository.UserAddressRepository
import com.leijendary.spring.template.iam.repository.UserRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

@Service
class AddressService(
    private val userAddressRepository: UserAddressRepository,
    private val userRepository: UserRepository
) {
    companion object {
        private const val CACHE_NAME = "address:v1"
        private val MAPPER = AddressMapper.INSTANCE
    }

    fun list(userId: UUID, pageable: Pageable): Page<AddressResponse> {
        return userAddressRepository
            .findByUserId(userId, pageable)
            .map { MAPPER.toResponse(it) }
    }

    @CachePut(value = [CACHE_NAME], key = "(#userId + ':' + #result.id)")
    fun create(userId: UUID, request: AddressRequest): AddressResponse {
        val user = userRepository.findByIdOrThrow(userId)
        val address = transactional {
            MAPPER
                .toEntity(request)
                .apply { this.user = user }
                .let { userAddressRepository.save(it) }
        }!!

        return MAPPER.toResponse(address)
    }

    @Cacheable(value = [CACHE_NAME], key = "(#userId + ':' + #id)")
    fun get(userId: UUID, id: UUID): AddressResponse {
        val address = userAddressRepository.findFirstByIdAndUserIdOrThrow(id, userId)

        return MAPPER.toResponse(address)
    }

    @CachePut(value = [CACHE_NAME], key = "(#userId + ':' + #id)")
    fun update(userId: UUID, id: UUID, request: AddressRequest): AddressResponse {
        val address = userAddressRepository.findFirstByIdAndUserIdOrThrow(id, userId)

        MAPPER.update(request, address)

        userAddressRepository.save(address)

        return MAPPER.toResponse(address)
    }

    @CacheEvict(value = [CACHE_NAME], key = "(#userId + ':' + #id)")
    fun delete(userId: UUID, id: UUID) = transactional {
        userAddressRepository
            .findFirstByIdAndUserIdOrThrow(id, userId)
            .let {
                userAddressRepository.delete(it)
            }
    }
}