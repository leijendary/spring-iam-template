package com.leijendary.spring.template.iam.model

enum class DevicePlatform(val value: String) {
    IOS("ios"),
    ANDROID("android"),
    WEB("web");

    companion object {
        fun from(value: String) = values().first { it.value == value }
    }

    override fun toString() = value
}
