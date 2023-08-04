package com.leijendary.spring.template.iam.api.v1.service

import com.leijendary.spring.template.iam.api.v1.mapper.UserProviderMapper
import com.leijendary.spring.template.iam.api.v1.model.UserProviderRequest
import com.leijendary.spring.template.iam.api.v1.model.UserProviderResponse
import com.leijendary.spring.template.iam.repository.UserProviderRepository
import com.leijendary.spring.template.iam.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserProviderService(
    private val userProviderRepository: UserProviderRepository,
    private val userRepository: UserRepository
) {
    fun list(userId: UUID, pageable: Pageable): Page<UserProviderResponse> {
        return userProviderRepository
            .findByUserId(userId, pageable)
            .map(UserProviderMapper.INSTANCE::toResponse)
    }

    fun create(userId: UUID, request: UserProviderRequest): UserProviderResponse {
        val user = userRepository.findCachedByIdOrThrow(userId)
        val userProvider = UserProviderMapper.INSTANCE.toEntity(request)
            .apply { this.user = user }
            .let(userProviderRepository::saveAndCache)

        return UserProviderMapper.INSTANCE.toResponse(userProvider)
    }

    fun get(userId: UUID, provider: String, type: String): UserProviderResponse {
        val userProvider = userProviderRepository
            .findFirstCachedByUserIdAndProviderAndTypeOrThrow(userId, provider, type)

        return UserProviderMapper.INSTANCE.toResponse(userProvider)
    }
}
