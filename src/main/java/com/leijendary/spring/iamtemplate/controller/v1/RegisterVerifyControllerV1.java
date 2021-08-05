package com.leijendary.spring.iamtemplate.controller.v1;

import com.leijendary.spring.iamtemplate.controller.AbstractController;
import com.leijendary.spring.iamtemplate.data.request.v1.VerifyRequestV1;
import com.leijendary.spring.iamtemplate.data.response.DataResponse;
import com.leijendary.spring.iamtemplate.data.response.v1.NextCodeV1;
import com.leijendary.spring.iamtemplate.flow.RegisterVerifyFlow;
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
@RequestMapping(BASE_API_PATH + "/v1/register/verify")
@RequiredArgsConstructor
@Api("API for the verifying the registration flow. After this, the user should go to the nominate password flow")
public class RegisterVerifyControllerV1 extends AbstractController {

    private final RegisterVerifyFlow registerVerifyFlow;

    @PostMapping
    @ApiOperation("Verify the user's registration. If the user doesnt have a password yet, a nominate pin" +
            "verification code will return")
    public CompletableFuture<DataResponse<NextCodeV1>> verify(
            @Valid @RequestBody final VerifyRequestV1 request) {
        final var verifyResponse = registerVerifyFlow.verifyV1(request);
        final var response = DataResponse.<NextCodeV1>builder()
                .data(verifyResponse)
                .object(NextCodeV1.class)
                .build();

        return completedFuture(response);
    }
}
