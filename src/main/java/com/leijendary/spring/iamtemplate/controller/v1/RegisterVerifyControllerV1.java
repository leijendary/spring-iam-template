package com.leijendary.spring.iamtemplate.controller.v1;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.leijendary.spring.iamtemplate.controller.AbstractController.BASE_API_PATH;

@RestController
@RequestMapping(BASE_API_PATH + "/v1/register/verify")
@RequiredArgsConstructor
@Api("API for the verifying the registration flow")
public class RegisterVerifyControllerV1 {
}
