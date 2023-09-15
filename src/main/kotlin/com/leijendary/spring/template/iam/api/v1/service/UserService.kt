package com.leijendary.spring.template.iam.api.v1.service

import com.leijendary.spring.template.iam.api.v1.mapper.UserMapper
import com.leijendary.spring.template.iam.api.v1.model.UserExclusionQueryRequest
import com.leijendary.spring.template.iam.api.v1.model.UserRequest
import com.leijendary.spring.template.iam.api.v1.model.UserResponse
import com.leijendary.spring.template.iam.core.config.properties.VerificationProperties
import com.leijendary.spring.template.iam.core.datasource.transactional
import com.leijendary.spring.template.iam.core.model.QueryRequest
import com.leijendary.spring.template.iam.entity.Account
import com.leijendary.spring.template.iam.entity.UserCredential
import com.leijendary.spring.template.iam.entity.Verification
import com.leijendary.spring.template.iam.generator.CodeGenerator
import com.leijendary.spring.template.iam.repository.AccountRepository
import com.leijendary.spring.template.iam.repository.RoleRepository
import com.leijendary.spring.template.iam.repository.UserRepository
import com.leijendary.spring.template.iam.repository.VerificationRepository
import com.leijendary.spring.template.iam.specification.UserListSpecification
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UserService(
    private val accountRepository: AccountRepository,
    private val roleRepository: RoleRepository,
    private val userRepository: UserRepository,
    private val verificationProperties: VerificationProperties,
    private val verificationRepository: VerificationRepository
) {
    fun list(
        queryRequest: QueryRequest,
        userExclusionQueryRequest: UserExclusionQueryRequest,
        pageable: Pageable
    ): Page<UserResponse> {
        val specification = UserListSpecification(queryRequest.query, userExclusionQueryRequest)
        val page = userRepository.findAll(specification, pageable)

        return page.map(UserMapper.INSTANCE::toResponse)
    }

    fun create(request: UserRequest): UserResponse {
        val role = roleRepository.findCachedByIdOrThrow(request.role!!.id!!)
        val account = request.account?.let {
            Account().apply {
                type = it.type!!
                status = Account.Status.ACTIVE
            }
        }
        val user = UserMapper.INSTANCE.toEntity(request).apply {
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

        val verification = Verification().apply {
            this.user = user
            this.code = CodeGenerator.UUID_GENERATOR.generate()
            this.field = field
            this.value = username
            this.type = Verification.Type.PASSWORD_NOMINATE
            this.expiresAt = verificationProperties.computeExpiration()
        }

        transactional {
            userRepository.saveAndCache(user)
            verificationRepository.save(verification)
        }

        return UserMapper.INSTANCE.toResponse(user)
    }

    fun get(id: UUID): UserResponse {
        val user = userRepository.findCachedByIdOrThrow(id)

        return UserMapper.INSTANCE.toResponse(user)
    }

    fun update(id: UUID, request: UserRequest): UserResponse {
        val role = roleRepository.findCachedByIdOrThrow(request.role!!.id!!)
        val user = userRepository.findByIdOrThrow(id).apply { this.role = role }

        UserMapper.INSTANCE.update(request, user)

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

        transactional { userRepository.saveAndCache(user) }

        return UserMapper.INSTANCE.toResponse(user)
    }

    @Transactional
    fun delete(id: UUID) {
        val user = userRepository.findByIdOrThrow(id)
        user.account?.let { accountRepository.softDelete(it) }

        userRepository.softDeleteAndEvict(user)
    }
}
