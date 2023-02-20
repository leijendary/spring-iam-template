package com.leijendary.spring.template.iam.api.v1.service

import com.leijendary.spring.template.iam.api.v1.mapper.ProfileMapper
import com.leijendary.spring.template.iam.api.v1.model.ProfileRequest
import com.leijendary.spring.template.iam.api.v1.model.ProfileResponse
import com.leijendary.spring.template.iam.core.exception.ResourceNotFoundException
import com.leijendary.spring.template.iam.core.extension.transactional
import com.leijendary.spring.template.iam.core.storage.S3Storage
import com.leijendary.spring.template.iam.repository.UserRepository
import org.springframework.cache.annotation.CachePut
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

@Service
class ProfileService(private val s3Storage: S3Storage, private val userRepository: UserRepository) {
    companion object {
        private const val CACHE_NAME = "profile:v1"
        private val MAPPER = ProfileMapper.INSTANCE
        private val SOURCE = listOf("data", "User", "id")
    }

    @CachePut(value = [CACHE_NAME], key = "#userId")
    fun detail(userId: String): ProfileResponse {
        val id = UUID.fromString(userId)
        val user = transactional(readOnly = true) {
            userRepository
                .findByIdOrNull(id)
                ?: throw ResourceNotFoundException(SOURCE, id)
        }!!

        return MAPPER.toResponse(user, s3Storage)
    }

    @CachePut(value = [CACHE_NAME], key = "#userId")
    fun update(userId: String, request: ProfileRequest): ProfileResponse {
        val id = UUID.fromString(userId)
        val user = transactional {
            userRepository
                .findByIdOrNull(id)
                ?.let {
                    MAPPER.update(request, it)

                    userRepository.save(it)
                }
                ?: throw ResourceNotFoundException(SOURCE, id)
        }!!

        return MAPPER.toResponse(user, s3Storage)
    }
}
