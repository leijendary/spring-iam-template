package com.leijendary.spring.iamtemplate.controller.v1;

import com.leijendary.spring.iamtemplate.controller.AbstractController;
import com.leijendary.spring.iamtemplate.data.request.v1.RegisterCustomerMobileRequestV1;
import com.leijendary.spring.iamtemplate.data.request.v1.RegisterVerificationResponseV1;
import com.leijendary.spring.iamtemplate.data.response.DataResponse;
import com.leijendary.spring.iamtemplate.service.RegisterCustomerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

import static com.leijendary.spring.iamtemplate.controller.AbstractController.BASE_API_PATH;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.springframework.http.HttpStatus.ACCEPTED;

@RestController
@RequestMapping(BASE_API_PATH + "/v1/register/customer")
@RequiredArgsConstructor
@Api("API for the registration flows of the customer")
public class RegisterCustomerControllerV1 extends AbstractController {

    private final RegisterCustomerService registerCustomerService;

    @PostMapping("mobile")
    @ResponseStatus(ACCEPTED)
    @ApiOperation("A customer account can register using this API. Although, this API " +
            "contains only the mobileNumber, emailAddress, deviceId, and preferredUsername. " +
            "Once registered, this API should send an SMS verification for the mobile number")
    public CompletableFuture<DataResponse<RegisterVerificationResponseV1>> mobile(
            final @RequestBody RegisterCustomerMobileRequestV1 request) {
        final var registerResponse = registerCustomerService.mobile(request);
        final var response = DataResponse.<RegisterVerificationResponseV1>builder()
                .data(registerResponse)
                .status(ACCEPTED)
                .object(RegisterVerificationResponseV1.class)
                .build();

        return completedFuture(response);
    }
}
