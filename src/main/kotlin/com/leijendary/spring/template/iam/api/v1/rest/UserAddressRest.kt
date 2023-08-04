package com.leijendary.spring.template.iam.api.v1.rest

import com.leijendary.spring.template.iam.api.v1.model.UserAddressResponse
import com.leijendary.spring.template.iam.api.v1.service.UserAddressService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/api/v1/users/{userId}/addresses")
@Tag(name = "User Address", description = "Addresses from the user based on the passed userId.")
class UserAddressRest(private val userAddressService: UserAddressService) {
    @GetMapping
    @Operation(summary = "Get the paginated list of the user's addresses.")
    fun list(@PathVariable userId: UUID, pageable: Pageable) = userAddressService.list(userId, pageable)

    @GetMapping("{id}")
    @Operation(summary = "Retrieves the address details from the database.")
    fun get(@PathVariable userId: UUID, @PathVariable id: UUID): UserAddressResponse {
        return userAddressService.get(userId, id)
    }
}
