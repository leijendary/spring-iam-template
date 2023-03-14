package com.leijendary.spring.template.iam.repository

import com.leijendary.spring.template.iam.entity.UserSocial
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserSocialRepository : JpaRepository<UserSocial, String> {
    fun findFirstByIdAndUserDeletedAtIsNull(id: String): UserSocial?

    fun existsByUserIdAndProvider(userId: UUID, provider: String): Boolean
}
