package com.leijendary.spring.template.iam.api.v1.service

import com.leijendary.spring.template.iam.api.v1.mapper.VerificationMapper
import com.leijendary.spring.template.iam.api.v1.model.Next
import com.leijendary.spring.template.iam.api.v1.model.VerificationCreateRequest
import com.leijendary.spring.template.iam.core.config.properties.VerificationProperties
import com.leijendary.spring.template.iam.core.datasource.transactional
import com.leijendary.spring.template.iam.generator.CodeGenerationStrategy
import com.leijendary.spring.template.iam.repository.VerificationRepository
import org.springframework.stereotype.Service

@Service
class VerificationService(
    private val verificationProperties: VerificationProperties,
    private val verificationRepository: VerificationRepository
) {
    companion object {
        private val MAPPER = VerificationMapper.INSTANCE
    }

    fun create(request: VerificationCreateRequest): Next {
        val field = request.field!!
        val value = request.value!!
        val type = request.type!!
        val generator = CodeGenerationStrategy.fromField(field)
        val verification = MAPPER.toEntity(request).apply {
            code = generator.generate()
            expiresAt = verificationProperties.computeExpiration()
        }

        transactional {
            verificationRepository.deleteAllByFieldAndValueAndType(field, value, type)
            verificationRepository.save(verification)
        }

        return Next(verification.type.value)
    }
}
