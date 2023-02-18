package com.leijendary.spring.template.iam.repository

import com.leijendary.spring.template.iam.entity.Verification
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface VerificationRepository : JpaRepository<Verification, Long> {
    fun deleteAllByUserIdAndType(userId: UUID, type: String)

    fun findFirstByCodeAndType(code: String, type: String): Verification
}
