package com.leijendary.spring.template.iam.api.v1.service

import com.leijendary.spring.template.iam.api.v1.model.AccountDeleteRequest
import com.leijendary.spring.template.iam.repository.AccountRepository
import com.leijendary.spring.template.iam.repository.AuthRepository
import com.leijendary.spring.template.iam.repository.UserRepository
import com.leijendary.spring.template.iam.repository.UserSocialRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class AccountService(
    private val accountRepository: AccountRepository,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val userSocialRepository: UserSocialRepository
) {
    @Transactional
    fun delete(userId: UUID, accountDeleteRequest: AccountDeleteRequest) {
        val account = accountRepository.findFirstByUsersIdOrThrow(userId).apply {
            deletedReason = accountDeleteRequest.reason
        }

        accountRepository.softDelete(account)

        account.users.forEach {
            it.deletedReason = accountDeleteRequest.reason

            authRepository.deleteByUserId(it.id!!)
            userRepository.softDeleteAndEvict(it)
            userSocialRepository.deleteByUserId(it.id!!)
        }
    }
}
