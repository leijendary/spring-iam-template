package com.leijendary.spring.template.iam.repository

import com.leijendary.spring.template.iam.entity.AuthRefresh
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface AuthRefreshRepository : JpaRepository<AuthRefresh, UUID>
