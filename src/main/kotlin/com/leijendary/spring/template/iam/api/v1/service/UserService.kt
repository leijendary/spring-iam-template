package com.leijendary.spring.template.iam.api.v1.service

import com.leijendary.spring.template.iam.api.v1.mapper.UserMapper
import com.leijendary.spring.template.iam.api.v1.model.UserExclusionQueryRequest
import com.leijendary.spring.template.iam.api.v1.model.UserRequest
import com.leijendary.spring.template.iam.api.v1.model.UserResponse
import com.leijendary.spring.template.iam.core.config.properties.VerificationProperties
import com.leijendary.spring.template.iam.core.extension.transactional
import com.leijendary.spring.template.iam.core.model.QueryRequest
import com.leijendary.spring.template.iam.core.storage.S3Storage
import com.leijendary.spring.template.iam.entity.Account
import com.leijendary.spring.template.iam.entity.UserCredential
import com.leijendary.spring.template.iam.entity.Verification
import com.leijendary.spring.template.iam.generator.CodeGenerationStrategy
import com.leijendary.spring.template.iam.model.Status
import com.leijendary.spring.template.iam.repository.AccountRepository
import com.leijendary.spring.template.iam.repository.RoleRepository
import com.leijendary.spring.template.iam.repository.UserRepository
import com.leijendary.spring.template.iam.repository.VerificationRepository
import com.leijendary.spring.template.iam.specification.UserListSpecification
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
    private val accountRepository: AccountRepository,
    private val roleRepository: RoleRepository,
    private val s3Storage: S3Storage,
    private val userRepository: UserRepository,
    private val verificationProperties: VerificationProperties,
    private val verificationRepository: VerificationRepository
) {
    companion object {
        private const val CACHE_NAME = "user:v1"
        private val MAPPER = UserMapper.INSTANCE
    }

    fun list(
        queryRequest: QueryRequest,
        userExclusionQueryRequest: UserExclusionQueryRequest,
        pageable: Pageable
    ): Page<UserResponse> {
        val specification = UserListSpecification(queryRequest.query, userExclusionQueryRequest)
        val page = transactional(readOnly = true) {
            userRepository.findAll(specification, pageable)
        }!!

        return page.map { MAPPER.toResponse(it, s3Storage) }
    }

    @CachePut(value = [CACHE_NAME], key = "#result.id")
    fun create(request: UserRequest): UserResponse {
        val role = transactional(readOnly = true) {
            request.role!!.id!!.let {
                roleRepository.findByIdOrThrow(it)
            }
        }
        val account = Account().apply {
            type = request.account!!.type!!
            status = Status.ACTIVE
        }
        val user = MAPPER.toEntity(request).apply {
            this.account = account
            this.role = role
        }
        val field = UserCredential.Type.EMAIL
        val username = user.getUsername(field)
        val credential = UserCredential().apply {
            this.user = user
            this.username = username
            this.type = field
        }

        user.credentials.add(credential)

        val generator = CodeGenerationStrategy.UUID_STRATEGY
        val verification = Verification().apply {
            this.user = user
            this.code = generator.generate()
            this.field = field
            this.value = username
            this.type = Verification.Type.PASSWORD_NOMINATE
            this.expiresAt = verificationProperties.computeExpiration()
        }

        transactional {
            userRepository.save(user)
            verificationRepository.save(verification)
        }

        return MAPPER.toResponse(user, s3Storage)
    }

    @Cacheable(value = [CACHE_NAME], key = "#id")
    fun get(id: UUID): UserResponse {
        val user = transactional(readOnly = true) {
            userRepository.findByIdOrThrow(id)
        }!!

        return MAPPER.toResponse(user, s3Storage)
    }

    @CachePut(value = [CACHE_NAME], key = "#result.id")
    fun update(id: UUID, request: UserRequest): UserResponse {
        val user = transactional(readOnly = true) {
            val role = request.role!!.id!!.let {
                roleRepository.findByIdOrThrow(it)
            }

            userRepository
                .findByIdOrThrow(id)
                .apply { this.role = role }
        }!!

        MAPPER.update(request, user)

        val field = UserCredential.Type.EMAIL
        val hasCredential = user.credentials.any { it.type == field }

        if (!hasCredential) {
            val username = user.getUsername(field)
            val credential = UserCredential().apply {
                this.user = user
                this.username = username
                this.type = field
            }

            user.credentials.add(credential)
        }

        transactional { userRepository.save(user) }

        return MAPPER.toResponse(user, s3Storage)
    }

    @CacheEvict(value = [CACHE_NAME], key = "#id")
    fun delete(id: UUID) = transactional {
        userRepository
            .findByIdOrThrow(id)
            .let { user ->
                user.account?.let { account ->
                    accountRepository.softDelete(account)
                }

                userRepository.softDelete(user)
            }
    }
}
