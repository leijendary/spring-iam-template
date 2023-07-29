package com.leijendary.spring.template.iam.api.v1.service

import com.leijendary.spring.template.iam.api.v1.mapper.AddressMapper
import com.leijendary.spring.template.iam.api.v1.model.AddressRequest
import com.leijendary.spring.template.iam.api.v1.model.AddressResponse
import com.leijendary.spring.template.iam.core.validator.CountryValidator
import com.leijendary.spring.template.iam.repository.UserAddressRepository
import com.leijendary.spring.template.iam.repository.UserRepository
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
    fun list(userId: UUID, pageable: Pageable): Page<AddressResponse> {
        return userAddressRepository
            .findByUserId(userId, pageable)
            .map(AddressMapper.INSTANCE::toResponse)
    }

    fun create(userId: UUID, request: AddressRequest): AddressResponse {
        val user = userRepository.findCachedByIdOrThrow(userId)
        val country = countryValidator.validateCode(request.countryCode!!)
        val address = AddressMapper.INSTANCE.toEntity(request, country)
            .apply { this.user = user }
            .let(userAddressRepository::saveAndCache)

        if (request.primary) {
            unsetOthersAsPrimary(address.id!!, userId)
        }

        return AddressMapper.INSTANCE.toResponse(address)
    }

    fun get(userId: UUID, id: UUID): AddressResponse {
        val address = userAddressRepository.findFirstCachedByIdAndUserIdOrThrow(id, userId)

        return AddressMapper.INSTANCE.toResponse(address)
    }

    fun update(userId: UUID, id: UUID, request: AddressRequest): AddressResponse {
        val address = userAddressRepository.findFirstByIdAndUserIdOrThrow(id, userId)
        val country = countryValidator.validateCode(request.countryCode!!)

        AddressMapper.INSTANCE.update(request, country, address)

        userAddressRepository.saveAndCache(address)

        if (request.primary) {
            unsetOthersAsPrimary(address.id!!, userId)
        }

        return AddressMapper.INSTANCE.toResponse(address)
    }

    @Transactional
    fun delete(userId: UUID, id: UUID) {
        userAddressRepository
            .findFirstByIdAndUserIdOrThrow(id, userId)
            .let(userAddressRepository::deleteAndEvict)
    }

    private fun unsetOthersAsPrimary(id: UUID, userId: UUID) {
        userAddressRepository.findAllByIdNotAndUserIdAndPrimary(id, userId).forEach {
            it.primary = false

            userAddressRepository.saveAndEvict(it)
        }
    }
}
