package com.leijendary.spring.iamtemplate.data;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ResetPasswordRequestV1 {

    @NotBlank(message = "validation.required")
    private String deviceId;

    @NotBlank(message = "validation.required")
    private String username;
}
