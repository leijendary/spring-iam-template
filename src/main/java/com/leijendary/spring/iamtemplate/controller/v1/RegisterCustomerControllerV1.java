package com.leijendary.spring.iamtemplate.controller.v1;

import com.leijendary.spring.iamtemplate.controller.AbstractController;
import com.leijendary.spring.iamtemplate.data.request.v1.AccountRequestV1;
import com.leijendary.spring.iamtemplate.data.request.v1.RegisterCustomerMobileRequestV1;
import com.leijendary.spring.iamtemplate.data.request.v1.UserRequestV1;
import com.leijendary.spring.iamtemplate.data.response.DataResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

import static com.leijendary.spring.iamtemplate.controller.AbstractController.BASE_API_PATH;
import static com.leijendary.spring.iamtemplate.data.AccountType.CUSTOMER;

@RestController
@RequestMapping(BASE_API_PATH + "/v1/register/customer")
@RequiredArgsConstructor
@Api("API for the registration flows of the customer")
public class RegisterCustomerControllerV1 extends AbstractController {

    @PostMapping("mobile")
    @ApiOperation("A customer account can register using this API. Although, this API " +
            "contains only the mobileNumber, emailAddress, deviceId, and preferredUsername. " +
            "Once registered, this API should send an SMS verification for the mobile number")
    public CompletableFuture<DataResponse<RegisterCustomerResponseV1>> mobile(
            final @RequestBody RegisterCustomerMobileRequestV1 request) {
        final var accountRequest = new AccountRequestV1(CUSTOMER);
        final var userRequest = new UserRequestV1();
    }
}
