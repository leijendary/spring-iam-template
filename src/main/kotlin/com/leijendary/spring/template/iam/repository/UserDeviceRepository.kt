package com.leijendary.spring.template.iam.repository

import com.leijendary.spring.template.iam.entity.UserDevice
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserDeviceRepository : JpaRepository<UserDevice, Long> {
    fun findFirstByToken(token: String): UserDevice?

    fun deleteByTokenAndUserId(token: String, userId: UUID)
}
