package com.leijendary.spring.template.iam.api.v1.model

data class NextCode(val next: String, val code: String? = null) {
    enum class Type(val value: String) {
        AUTHENTICATE("authenticate");

        override fun toString(): String = value
    }
}
