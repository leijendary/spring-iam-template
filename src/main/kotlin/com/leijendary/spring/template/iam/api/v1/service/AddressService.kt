package com.leijendary.spring.template.iam.api.v1.service

import com.leijendary.spring.template.iam.api.v1.mapper.AddressMapper
import com.leijendary.spring.template.iam.api.v1.model.AddressRequest
import com.leijendary.spring.template.iam.api.v1.model.AddressResponse
import com.leijendary.spring.template.iam.core.datasource.transactional
import com.leijendary.spring.template.iam.core.validator.CountryValidator
import com.leijendary.spring.template.iam.repository.UserAddressRepository
import com.leijendary.spring.template.iam.repository.UserRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class AddressService(
    private val countryValidator: CountryValidator,
    private val userAddressRepository: UserAddressRepository,
    private val userRepository: UserRepository
) {
    companion object {
        private const val CACHE_NAME = "address:v1"
    }

    fun list(userId: UUID, pageable: Pageable): Page<AddressResponse> {
        return userAddressRepository
            .findByUserId(userId, pageable)
            .map { AddressMapper.INSTANCE.toResponse(it) }
    }

    @CachePut(value = [CACHE_NAME], key = "(#userId + ':' + #result.id)")
    fun create(userId: UUID, request: AddressRequest): AddressResponse {
        val user = userRepository.findByIdOrThrow(userId)
        val country = countryValidator.validateCode(request.countryCode!!)
        val address = transactional {
            val address = AddressMapper.INSTANCE
                .toEntity(request, country)
                .apply { this.user = user }
                .let { userAddressRepository.save(it) }

            if (request.primary) {
                userAddressRepository.unsetOthersAsPrimary(address.id!!)
            }

            address
        }!!

        return AddressMapper.INSTANCE.toResponse(address)
    }

    @Cacheable(value = [CACHE_NAME], key = "(#userId + ':' + #id)")
    fun get(userId: UUID, id: UUID): AddressResponse {
        val address = userAddressRepository.findFirstByIdAndUserIdOrThrow(id, userId)

        return AddressMapper.INSTANCE.toResponse(address)
    }

    @CachePut(value = [CACHE_NAME], key = "(#userId + ':' + #id)")
    fun update(userId: UUID, id: UUID, request: AddressRequest): AddressResponse {
        val address = userAddressRepository.findFirstByIdAndUserIdOrThrow(id, userId)
        val country = countryValidator.validateCode(request.countryCode!!)

        AddressMapper.INSTANCE.update(request, country, address)

        transactional {
            userAddressRepository.save(address)

            if (request.primary) {
                userAddressRepository.unsetOthersAsPrimary(address.id!!)
            }
        }

        return AddressMapper.INSTANCE.toResponse(address)
    }

    @CacheEvict(value = [CACHE_NAME], key = "(#userId + ':' + #id)")
    @Transactional
    fun delete(userId: UUID, id: UUID) {
        userAddressRepository
            .findFirstByIdAndUserIdOrThrow(id, userId)
            .let { userAddressRepository.delete(it) }
    }
}
