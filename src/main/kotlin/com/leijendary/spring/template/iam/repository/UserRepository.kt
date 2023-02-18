package com.leijendary.spring.template.iam.repository

import com.leijendary.spring.template.iam.core.repository.SoftDeleteRepository
import com.leijendary.spring.template.iam.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.*

interface UserRepository : JpaRepository<User, UUID>, SoftDeleteRepository<User>, JpaSpecificationExecutor<User>
