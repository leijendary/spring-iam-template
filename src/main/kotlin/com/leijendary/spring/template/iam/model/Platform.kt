package com.leijendary.spring.template.iam.model

enum class Platform(val value: String) {
    IOS("ios"),
    ANDROID("android"),
    WEB("web");

    override fun toString() = value
}
