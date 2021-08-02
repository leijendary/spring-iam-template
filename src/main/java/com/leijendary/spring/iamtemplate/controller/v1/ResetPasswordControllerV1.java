package com.leijendary.spring.iamtemplate.controller.v1;

import com.leijendary.spring.iamtemplate.data.ResetPasswordRequestV1;
import com.leijendary.spring.iamtemplate.data.request.v1.NominatePasswordRequestV1;
import com.leijendary.spring.iamtemplate.data.response.DataResponse;
import com.leijendary.spring.iamtemplate.data.response.v1.VerificationResponseV1;
import com.leijendary.spring.iamtemplate.flow.ResetPasswordFlow;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.util.concurrent.CompletableFuture;

import static com.leijendary.spring.iamtemplate.controller.AbstractController.BASE_API_PATH;
import static java.util.concurrent.CompletableFuture.completedFuture;

@RestController
@RequestMapping(BASE_API_PATH + "/v1/reset-password")
@RequiredArgsConstructor
@Api("Reset password API resource. Two calls; initiate the reset password flow, then verify.")
public class ResetPasswordControllerV1 {

    private final ResetPasswordFlow resetPasswordFlow;

    @PostMapping
    @ApiOperation("Create a verification code for the reset password flow")
    public CompletableFuture<DataResponse<VerificationResponseV1>> initiate(
            @Valid @RequestBody final ResetPasswordRequestV1 request) {
        final var verifyResponse = resetPasswordFlow.initiateV1(request);
        final var response = DataResponse.<VerificationResponseV1>builder()
                .data(verifyResponse)
                .object(VerificationResponseV1.class)
                .build();

        return completedFuture(response);
    }
}
