package com.leijendary.spring.template.iam.api.v1.rest

import com.leijendary.spring.template.iam.api.v1.service.PictureService
import com.leijendary.spring.template.iam.core.util.RequestContext.userIdOrThrow
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/picture")
@Tag(
    name = "Picture",
    description = "Picture API reference. There is no picture rendering included here, only storage links."
)
@SecurityRequirement(name = AUTHORIZATION)
class PictureRest(private val pictureService: PictureService) {
    @GetMapping("link")
    @Operation(summary = "Get a link to upload a new profile picture. The link is only valid for PUT requests.")
    fun link() = pictureService.link(userIdOrThrow)

    @PutMapping("link")
    @Operation(
        summary = """
            Update the profile picture by updating the profile image url based on the previous upload.
            This will return the updated URL.
        """
    )
    fun update() = pictureService.update(userIdOrThrow)
}
