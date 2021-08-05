package com.leijendary.spring.iamtemplate.controller.v1;

import com.leijendary.spring.iamtemplate.controller.AbstractController;
import com.leijendary.spring.iamtemplate.data.request.v1.ProfileRequestV1;
import com.leijendary.spring.iamtemplate.data.response.DataResponse;
import com.leijendary.spring.iamtemplate.data.response.v1.ProfileResponseV1;
import com.leijendary.spring.iamtemplate.flow.ProfileFlow;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.concurrent.CompletableFuture;

import static com.leijendary.spring.iamtemplate.controller.AbstractController.BASE_API_PATH;
import static java.util.concurrent.CompletableFuture.completedFuture;

@RestController
@RequestMapping(BASE_API_PATH + "/v1/profile")
@RequiredArgsConstructor
@Api("Profile API reference. Anything regarding the profile activity are included here")
public class ProfileControllerV1 extends AbstractController {

    private final ProfileFlow profileFlow;

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_urn:profile:detail:v1')")
    @ApiOperation("Get the current user's profile details")
    public CompletableFuture<DataResponse<ProfileResponseV1>> detail() {
        final var profileResponse = profileFlow.detailV1();
        final var response = DataResponse.<ProfileResponseV1>builder()
                .data(profileResponse)
                .object(ProfileResponseV1.class)
                .build();

        return completedFuture(response);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('SCOPE_urn:profile:update:v1')")
    @ApiOperation("Update the current user's profile details except those fields that are related to the username " +
            "that needs verification like email and mobile number")
    public CompletableFuture<DataResponse<ProfileResponseV1>> update(
            @Valid @RequestBody final ProfileRequestV1 request) {
        final var profileResponse = profileFlow.updateV1(request);
        final var response = DataResponse.<ProfileResponseV1>builder()
                .data(profileResponse)
                .object(ProfileResponseV1.class)
                .build();

        return completedFuture(response);
    }
}
