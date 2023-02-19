package com.leijendary.spring.template.iam.repository

import com.leijendary.spring.template.iam.entity.AuthAccess
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface AuthAccessRepository : JpaRepository<AuthAccess, UUID>
