package com.leijendary.spring.template.iam.api.v1.rest

import com.leijendary.spring.template.iam.api.v1.model.AddressRequest
import com.leijendary.spring.template.iam.api.v1.model.AddressResponse
import com.leijendary.spring.template.iam.api.v1.service.AddressService
import com.leijendary.spring.template.iam.core.util.RequestContext.userIdOrThrow
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/addresses")
@Tag(name = "Address", description = "Address API reference for the currently logged in user.")
class AddressRest(private val addressService: AddressService) {
    @GetMapping
    @Operation(summary = "Get the paginated list of the user's addresses.")
    fun list(pageable: Pageable) = addressService.list(userIdOrThrow, pageable)

    @PostMapping
    @ResponseStatus(CREATED)
    @Operation(summary = "Saves an address attached to the user into the database.")
    fun create(@Valid @RequestBody request: AddressRequest) = addressService.create(userIdOrThrow, request)

    @GetMapping("{id}")
    @Operation(summary = "Retrieves the address details from the database.")
    fun get(@PathVariable id: UUID) = addressService.get(userIdOrThrow, id)

    @PutMapping("{id}")
    @Operation(summary = "Updates the address attached to the user into the database.")
    fun update(@PathVariable id: UUID, @Valid @RequestBody request: AddressRequest): AddressResponse {
        return addressService.update(userIdOrThrow, id, request)
    }

    @DeleteMapping("{id}")
    @ResponseStatus(NO_CONTENT)
    @Operation(summary = "Deletes the user's address from the database.")
    fun delete(@PathVariable id: UUID) = addressService.delete(userIdOrThrow, id)
}
