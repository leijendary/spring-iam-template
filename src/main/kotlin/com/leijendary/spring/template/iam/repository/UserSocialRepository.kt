package com.leijendary.spring.template.iam.repository

import com.leijendary.spring.template.iam.entity.UserSocial
import org.springframework.data.jpa.repository.JpaRepository

interface UserSocialRepository : JpaRepository<UserSocial, String> {
    fun findFirstByIdAndUserDeletedAtIsNull(id: String): UserSocial?
}
