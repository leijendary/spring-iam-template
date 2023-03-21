package com.leijendary.spring.template.iam.repository

import com.leijendary.spring.template.iam.entity.UserSocial
import com.leijendary.spring.template.iam.entity.UserSocial.Provider
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserSocialRepository : JpaRepository<UserSocial, String> {
    fun findFirstByIdAndUserDeletedAtIsNull(id: String): UserSocial?

    fun existsByUserIdAndProvider(userId: UUID, provider: Provider): Boolean
}
