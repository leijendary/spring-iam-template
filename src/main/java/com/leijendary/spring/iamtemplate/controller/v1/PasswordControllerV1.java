package com.leijendary.spring.iamtemplate.controller.v1;

import com.leijendary.spring.iamtemplate.controller.AbstractController;
import com.leijendary.spring.iamtemplate.data.ResetPasswordRequestV1;
import com.leijendary.spring.iamtemplate.data.request.v1.NominatePasswordRequestV1;
import com.leijendary.spring.iamtemplate.data.request.v1.VerifyRequestV1;
import com.leijendary.spring.iamtemplate.data.response.DataResponse;
import com.leijendary.spring.iamtemplate.data.response.v1.NextCodeV1;
import com.leijendary.spring.iamtemplate.flow.PasswordFlow;
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
@RequestMapping(BASE_API_PATH + "/v1/password")
@RequiredArgsConstructor
@Api("Password API reference. All actions regarding password should be here")
public class PasswordControllerV1 extends AbstractController {

    private final PasswordFlow passwordFlow;

    @PostMapping("reset")
    @ApiOperation("Create a verification code for the reset password flow")
    public CompletableFuture<DataResponse<NextCodeV1>> reset(
            @Valid @RequestBody final ResetPasswordRequestV1 request) {
        final var verifyResponse = passwordFlow.resetV1(request);
        final var response = DataResponse.<NextCodeV1>builder()
                .data(verifyResponse)
                .object(NextCodeV1.class)
                .build();

        return completedFuture(response);
    }

    @PostMapping("reset/verify")
    @ApiOperation("Use the verification code from the reset password initiation to create a verification" +
            "code for the nominate password process")
    public CompletableFuture<DataResponse<NextCodeV1>> resetVerify(
            @Valid @RequestBody final VerifyRequestV1 request) {
        final var verifyResponse = passwordFlow.resetVerifyV1(request);
        final var response = DataResponse.<NextCodeV1>builder()
                .data(verifyResponse)
                .object(NextCodeV1.class)
                .build();

        return completedFuture(response);
    }

    @PostMapping("nominate")
    @ApiOperation("Create a password using the verification code sent")
    public CompletableFuture<DataResponse<NextCodeV1>> nominate(
            @Valid @RequestBody final NominatePasswordRequestV1 request) {
        final var verifyResponse = passwordFlow.nominateV1(request);
        final var response = DataResponse.<NextCodeV1>builder()
                .data(verifyResponse)
                .object(NextCodeV1.class)
                .build();

        return completedFuture(response);
    }
}
