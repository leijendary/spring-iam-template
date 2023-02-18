package com.leijendary.spring.template.iam.api.v1.model

class UserExclusionQueryRequest {
    var exclude = Exclusion()

    class Exclusion {
        var withAccounts: Boolean = false
    }
}
