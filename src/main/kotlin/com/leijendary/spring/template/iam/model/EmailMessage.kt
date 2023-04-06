package com.leijendary.spring.template.iam.model

data class EmailMessage(val to: String, val template: String, val parameters: Map<String, String>)
