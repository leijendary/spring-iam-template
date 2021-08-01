package com.leijendary.spring.iamtemplate.data.request.v1;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class VerifyRequestV1 {

    @NotBlank(message = "validation.required")
    private String verificationId;

    @NotBlank(message = "validation.required")
    private String code;
}
