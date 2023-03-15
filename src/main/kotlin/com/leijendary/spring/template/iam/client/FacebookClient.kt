package com.leijendary.spring.template.iam.client

import com.leijendary.spring.template.iam.model.FacebookProfileResponse
import feign.CollectionFormat.CSV
import org.springframework.cloud.openfeign.CollectionFormat
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "facebook", url = "\${auth.social.facebook.url}")
interface FacebookClient {
    @GetMapping("me")
    @CollectionFormat(CSV)
    fun profile(
        @RequestParam fields: Set<String>,
        @RequestParam(name = "access_token") accessToken: String
    ): FacebookProfileResponse
}