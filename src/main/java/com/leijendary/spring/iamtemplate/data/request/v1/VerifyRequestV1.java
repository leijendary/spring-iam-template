package com.leijendary.spring.iamtemplate.data.request.v1;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
public class VerifyRequestV1 {

    @Min(message = "validation.id.invalid", value = 1)
    private long verificationId;

    @NotBlank(message = "validation.required")
    private String code;

    @NotBlank(message = "validation.required")
    private String deviceId;
}
