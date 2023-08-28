package com.leijendary.spring.template.iam.api.v1.service

import com.leijendary.spring.template.iam.api.v1.mapper.VerificationMapper
import com.leijendary.spring.template.iam.api.v1.model.NextResponse
import com.leijendary.spring.template.iam.api.v1.model.VerificationCreateRequest
import com.leijendary.spring.template.iam.core.config.properties.VerificationProperties
import com.leijendary.spring.template.iam.core.datasource.transactional
import com.leijendary.spring.template.iam.core.exception.StatusException
import com.leijendary.spring.template.iam.generator.CodeGenerationStrategy
import com.leijendary.spring.template.iam.repository.VerificationRepository
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.stereotype.Service

private val timeoutSource = listOf("data", "Verification", "createdAt")

@Service
class VerificationService(
    private val verificationProperties: VerificationProperties,
    private val verificationRepository: VerificationRepository
) {
    fun create(request: VerificationCreateRequest): NextResponse {
        val field = request.field!!
        val value = request.value!!
        val type = request.type!!
        val existing = verificationRepository.findFirstByFieldAndValueAndType(field, value, type)
        val timeout = verificationProperties.computeTimeout()
        val isTimedOut = existing?.createdAt?.isAfter(timeout) ?: false

        if (isTimedOut) {
            throw StatusException(timeoutSource, "validation.verification.timeout", BAD_REQUEST)
        }

        val generator = CodeGenerationStrategy.fromField(field)
        val verification = VerificationMapper.INSTANCE.toEntity(request).apply {
            code = generator.generate()
            expiresAt = verificationProperties.computeExpiration()
        }

        transactional {
            verificationRepository.deleteAllByFieldAndValueAndType(field, value, type)
            verificationRepository.save(verification)
        }

        return NextResponse(verification.type.value)
    }
}
