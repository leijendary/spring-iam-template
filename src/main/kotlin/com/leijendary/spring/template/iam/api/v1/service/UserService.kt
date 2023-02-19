package com.leijendary.spring.template.iam.api.v1.service

import com.leijendary.spring.template.iam.api.v1.mapper.UserMapper
import com.leijendary.spring.template.iam.api.v1.model.UserExclusionQueryRequest
import com.leijendary.spring.template.iam.api.v1.model.UserRequest
import com.leijendary.spring.template.iam.api.v1.model.UserResponse
import com.leijendary.spring.template.iam.core.config.properties.VerificationProperties
import com.leijendary.spring.template.iam.core.exception.ResourceNotFoundException
import com.leijendary.spring.template.iam.core.extension.transactional
import com.leijendary.spring.template.iam.core.model.QueryRequest
import com.leijendary.spring.template.iam.entity.Account
import com.leijendary.spring.template.iam.entity.User
import com.leijendary.spring.template.iam.entity.UserCredential
import com.leijendary.spring.template.iam.entity.Verification
import com.leijendary.spring.template.iam.event.CredentialEvent
import com.leijendary.spring.template.iam.generator.CodeGenerationStrategy
import com.leijendary.spring.template.iam.repository.AccountRepository
import com.leijendary.spring.template.iam.repository.RoleRepository
import com.leijendary.spring.template.iam.repository.UserRepository
import com.leijendary.spring.template.iam.repository.VerificationRepository
import com.leijendary.spring.template.iam.specification.UserListSpecification
import com.leijendary.spring.template.iam.util.Status
import com.leijendary.spring.template.iam.util.VerificationType
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
    private val accountRepository: AccountRepository,
    private val credentialEvent: CredentialEvent,
    private val roleRepository: RoleRepository,
    private val userRepository: UserRepository,
    private val verificationProperties: VerificationProperties,
    private val verificationRepository: VerificationRepository
) {
    companion object {
        private const val CACHE_NAME = "user:v1"
        private val MAPPER = UserMapper.INSTANCE
        private val USER_SOURCE = listOf("data", "User", "id")
        private val ROLE_SOURCE = listOf("data", "Role", "id")
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

        return page.map { MAPPER.toResponse(it) }
    }

    @CachePut(value = [CACHE_NAME], key = "#result.id")
    fun create(request: UserRequest): UserResponse {
        val role = transactional(readOnly = true) {
            request.role!!.id!!.let {
                roleRepository
                    .findByIdOrNull(it)
                    ?: throw ResourceNotFoundException(ROLE_SOURCE, it)
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
        val field = request.preferredUsername!!
        val username = user.getUsername(field)

        UserCredential()
            .apply {
                this.user = user
                this.username = username
                this.type = field
            }
            .run {
                user.credentials.add(this)
            }

        val generator = CodeGenerationStrategy.fromField(field)
        val code = generator.generate()
        val verification = Verification().apply {
            this.user = user
            this.code = code
            this.field = field
            type = VerificationType.REGISTRATION
            expiresAt = verificationProperties.computeExpiration()
        }

        transactional {
            userRepository.save(user)
            verificationRepository.save(verification)
        }

        credentialEvent.verify(verification)

        return MAPPER.toResponse(user)
    }

    @Cacheable(value = [CACHE_NAME], key = "#id")
    fun get(id: UUID): UserResponse {
        val user = transactional(readOnly = true) {
            userRepository
                .findByIdOrNull(id)
                ?: throw ResourceNotFoundException(USER_SOURCE, id)
        }!!

        return MAPPER.toResponse(user)
    }

    @CachePut(value = [CACHE_NAME], key = "#result.id")
    fun update(id: UUID, request: UserRequest): UserResponse {
        lateinit var user: User

        transactional(readOnly = true) {
            val role = request.role!!.id!!.let {
                roleRepository
                    .findByIdOrNull(it)
                    ?: throw ResourceNotFoundException(ROLE_SOURCE, it)
            }
            user = userRepository
                .findByIdOrNull(id)
                ?.let {
                    it.role = role

                    it
                }
                ?: throw ResourceNotFoundException(USER_SOURCE, id)
        }

        MAPPER.update(request, user)

        val field = request.preferredUsername!!
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

        transactional {
            userRepository.save(user)
        }

        return MAPPER.toResponse(user)
    }

    @CacheEvict(value = [CACHE_NAME], key = "#id")
    fun delete(id: UUID) {
        transactional {
            userRepository
                .findByIdOrNull(id)
                ?.let { user ->
                    user.account?.let { account ->
                        accountRepository.softDelete(account)
                    }

                    userRepository.softDelete(user)
                }
                ?: throw ResourceNotFoundException(USER_SOURCE, id)
        }
    }
}
