package com.leijendary.spring.template.iam.api.v1.model

data class NextResponse(val next: String) {
    enum class Type(val value: String) {
        AUTHENTICATE("authenticate");
    }
}
