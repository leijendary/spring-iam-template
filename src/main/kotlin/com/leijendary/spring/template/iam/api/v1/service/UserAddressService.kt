package com.leijendary.spring.template.iam.api.v1.service

import com.leijendary.spring.template.iam.api.v1.mapper.UserAddressMapper
import com.leijendary.spring.template.iam.api.v1.model.UserAddressResponse
import com.leijendary.spring.template.iam.repository.UserAddressRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserAddressService(private val userAddressRepository: UserAddressRepository) {
    fun list(userId: UUID, pageable: Pageable): Page<UserAddressResponse> {
        return userAddressRepository
            .findByUserId(userId, pageable)
            .map(UserAddressMapper.INSTANCE::toResponse)
    }

    fun get(userId: UUID, id: UUID): UserAddressResponse {
        val address = userAddressRepository.findFirstCachedByIdAndUserIdOrThrow(id, userId)

        return UserAddressMapper.INSTANCE.toResponse(address)
    }
}
