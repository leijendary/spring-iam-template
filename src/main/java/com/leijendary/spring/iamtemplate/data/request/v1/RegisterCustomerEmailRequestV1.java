package com.leijendary.spring.iamtemplate.data.request.v1;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class RegisterCustomerEmailRequestV1 {

    @NotBlank(message = "validation.required")
    @Email(message = "validation.emailAddress.invalid")
    private String emailAddress;

    @NotBlank(message = "validation.required")
    private String deviceId;
}
