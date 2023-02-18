package com.leijendary.spring.template.iam.util

/**
 * The fields here can also be used as an identifier for the API consumer's next step
 */
object VerificationType {
    const val REGISTRATION = "registration"
    const val VERIFICATION = "verification"
    const val NOMINATE_PASSWORD = "nominatePassword"
    const val AUTHENTICATE = "authenticate"
    const val RESET_PASSWORD = "resetPassword"
}
