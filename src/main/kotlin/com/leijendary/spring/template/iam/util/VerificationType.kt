package com.leijendary.spring.template.iam.util

/**
 * The fields here can also be used as an identifier for the API consumer's next step
 */
object VerificationType {
    const val REGISTRATION = "registration"
    const val VERIFICATION = "verification"
    const val AUTHENTICATE = "authenticate"
    const val PASSWORD_NOMINATE = "passwordNominate"
    const val PASSWORD_RESET = "passwordReset"
    const val PASSWORD_CHANGE_VERIFY = "passwordChangeVerify"
    const val PASSWORD_CHANGE = "passwordChange"
    const val EMAIL_VERIFY = "emailVerify"
    const val PHONE_VERIFY = "phoneVerify"
}
