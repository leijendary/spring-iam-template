package com.leijendary.spring.template.iam.core.model

import java.io.Serializable

sealed interface Response : Serializable {
    companion object {
        @JvmStatic
        val serialVersionUID = -1L
    }
}
