package com.leijendary.spring.template.iam.model

data class EmailMessage(val to: String, val subject: String, val htmlText: String, val plainText: String?)
