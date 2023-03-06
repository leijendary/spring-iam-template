package com.leijendary.spring.template.iam.api.v1.service

import com.leijendary.spring.template.iam.api.v1.facade.RegisterCustomerFacade
import com.leijendary.spring.template.iam.api.v1.mapper.UserMapper
import com.leijendary.spring.template.iam.api.v1.model.NextCode
import com.leijendary.spring.template.iam.api.v1.model.RegisterCustomerEmailRequest
import com.leijendary.spring.template.iam.api.v1.model.RegisterCustomerFullRequest
import com.leijendary.spring.template.iam.api.v1.model.RegisterCustomerPhoneRequest
import com.leijendary.spring.template.iam.entity.User
import com.leijendary.spring.template.iam.entity.UserCredential
import com.leijendary.spring.template.iam.util.VerificationType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class RegisterCustomerService(
    private val passwordEncoder: PasswordEncoder,
    private val registerCustomerFacade: RegisterCustomerFacade,
) {
    companion object {
        private val MAPPER = UserMapper.INSTANCE
    }

    fun phone(request: RegisterCustomerPhoneRequest): NextCode {
        val user = MAPPER.from(request)

        return register(user, UserCredential.Type.PHONE, request.deviceId!!)
    }

    fun email(request: RegisterCustomerEmailRequest): NextCode {
        val user = MAPPER.from(request)

        return register(user, UserCredential.Type.EMAIL, request.deviceId!!)
    }

    fun register(request: RegisterCustomerFullRequest): NextCode {
        val user = MAPPER.from(request)
        val field = request.preferredUsername!!
        val credentialType = UserCredential.Type.valueOf(field)
        val password = passwordEncoder.encode(request.password!!)

        return register(user, credentialType, request.deviceId!!, password)
    }

    private fun register(user: User, type: UserCredential.Type, deviceId: String, password: String? = null): NextCode {
        registerCustomerFacade.register(user, type, deviceId, password)

        return NextCode(VerificationType.VERIFICATION)
    }
}
