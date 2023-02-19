package com.leijendary.spring.template.iam.repository

import com.leijendary.spring.template.iam.entity.Auth
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface AuthRepository : JpaRepository<Auth, Long> {
    fun findFirstByAccessId(accessId: UUID): Auth?

    fun findFirstByRefreshId(refreshId: UUID): Auth?

    fun deleteByDeviceId(deviceId: String)
}
