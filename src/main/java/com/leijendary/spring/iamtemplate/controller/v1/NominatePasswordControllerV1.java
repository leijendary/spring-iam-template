package com.leijendary.spring.iamtemplate.controller.v1;

import com.leijendary.spring.iamtemplate.data.request.v1.NominatePasswordRequestV1;
import com.leijendary.spring.iamtemplate.data.response.DataResponse;
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

@RestController
@RequestMapping(BASE_API_PATH + "/v1/nominate-password")
@RequiredArgsConstructor
@Api("Nominate password API resource")
public class NominatePasswordControllerV1 {

    @PostMapping
    @ApiOperation("Create a password using the verification code sent")
    public CompletableFuture<DataResponse<?>> nominate(@Valid @RequestBody final NominatePasswordRequestV1 request) {

    }
}
