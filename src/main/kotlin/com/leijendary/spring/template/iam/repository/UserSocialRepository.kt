package com.leijendary.spring.template.iam.repository

import com.leijendary.spring.template.iam.entity.UserSocial
import com.leijendary.spring.template.iam.entity.UserSocial.Provider
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.transaction.annotation.Transactional
import java.util.*

interface UserSocialRepository : JpaRepository<UserSocial, String> {
    @Transactional(readOnly = true)
    fun findFirstByIdAndUserDeletedAtIsNull(id: String): UserSocial?

    @Transactional(readOnly = true)
    fun existsByUserIdAndProvider(userId: UUID, provider: Provider): Boolean
}
