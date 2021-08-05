package com.leijendary.spring.iamtemplate.data.request.v1;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ProfileRequestV1 {

    @NotBlank(message = "validation.required")
    private String firstName;

    @NotBlank(message = "validation.required")
    private String middleName;

    @NotBlank(message = "validation.required")
    private String lastName;
}
