package com.leijendary.spring.template.iam.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode

class FacebookProfileResponse(
    val id: String,

    @field:JsonProperty("first_name")
    val firstName: String? = null,

    @field:JsonProperty("last_name")
    val lastName: String? = null,

    val email: String,
) {
    private var picture: String? = null

    fun setPicture(picture: JsonNode) {
        this.picture = picture
            .get("data")
            ?.get("url")
            ?.asText()
    }

    fun getPicture() = this.picture
}