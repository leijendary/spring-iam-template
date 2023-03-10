package com.leijendary.spring.template.iam.model

enum class SocialProvider(val value: String) {
    APPLE("apple"),
    FACEBOOK("facebook"),
    GOOGLE("google");

    companion object {
        fun from(value: String) = values().first { it.value == value }
    }

    override fun toString() = value
}
