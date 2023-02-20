package com.leijendary.spring.template.iam.api.v1.service

import com.leijendary.spring.template.iam.api.v1.mapper.ProfileMapper
import com.leijendary.spring.template.iam.api.v1.model.ProfileRequest
import com.leijendary.spring.template.iam.api.v1.model.ProfileResponse
import com.leijendary.spring.template.iam.core.extension.transactional
import com.leijendary.spring.template.iam.core.storage.S3Storage
import com.leijendary.spring.template.iam.repository.UserRepository
import org.springframework.cache.annotation.CachePut
import org.springframework.stereotype.Service
import java.util.*

@Service
class ProfileService(private val s3Storage: S3Storage, private val userRepository: UserRepository) {
    companion object {
        private const val CACHE_NAME = "profile:v1"
        private val MAPPER = ProfileMapper.INSTANCE
    }

    @CachePut(value = [CACHE_NAME], key = "#userId")
    fun detail(userId: String): ProfileResponse {
        val id = UUID.fromString(userId)
        val user = transactional(readOnly = true) {
            userRepository.findByIdOrThrow(id)
        }!!

        return MAPPER.toResponse(user, s3Storage)
    }

    @CachePut(value = [CACHE_NAME], key = "#userId")
    fun update(userId: String, request: ProfileRequest): ProfileResponse {
        val id = UUID.fromString(userId)
        val user = transactional {
            userRepository
                .findByIdOrThrow(id)
                .let {
                    MAPPER.update(request, it)

                    userRepository.save(it)
                }
        }!!

        return MAPPER.toResponse(user, s3Storage)
    }
}
