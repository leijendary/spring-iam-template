package com.leijendary.spring.template.iam.repository

import com.leijendary.spring.template.iam.core.repository.SoftDeleteRepository
import com.leijendary.spring.template.iam.entity.Account
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface AccountRepository : JpaRepository<Account, UUID>, SoftDeleteRepository<Account>
