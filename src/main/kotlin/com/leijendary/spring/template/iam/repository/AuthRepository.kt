package com.leijendary.spring.template.iam.repository

import com.leijendary.spring.template.iam.entity.Auth
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface AuthRepository : JpaRepository<Auth, Long> {
    @Transactional(readOnly = true)
    fun findFirstByAccessId(accessId: UUID): Auth?

    @Transactional(readOnly = true)
    fun findFirstByRefreshId(refreshId: UUID): Auth?
}
