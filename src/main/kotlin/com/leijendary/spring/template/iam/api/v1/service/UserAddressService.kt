package com.leijendary.spring.template.iam.api.v1.service

import com.leijendary.spring.template.iam.api.v1.mapper.AddressMapper
import com.leijendary.spring.template.iam.api.v1.model.AddressResponse
import com.leijendary.spring.template.iam.repository.UserAddressRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserAddressService(private val userAddressRepository: UserAddressRepository) {
    companion object {
        private const val CACHE_NAME = "address:v1"
    }

    fun list(userId: UUID, pageable: Pageable): Page<AddressResponse> {
        return userAddressRepository
            .findByUserId(userId, pageable)
            .map(AddressMapper.INSTANCE::toResponse)
    }

    @Cacheable(value = [CACHE_NAME], key = "(#userId + ':' + #id)")
    fun get(userId: UUID, id: UUID): AddressResponse {
        val address = userAddressRepository.findFirstByIdAndUserIdOrThrow(id, userId)

        return AddressMapper.INSTANCE.toResponse(address)
    }
}
