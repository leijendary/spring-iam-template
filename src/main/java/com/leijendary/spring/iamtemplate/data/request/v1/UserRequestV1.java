package com.leijendary.spring.iamtemplate.data.request.v1;

import com.leijendary.spring.iamtemplate.validator.annotation.v1.MobileNumber;
import com.leijendary.spring.iamtemplate.validator.annotation.v1.PreferredUsernameV1;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UserRequestV1 {

    @Valid
    private AccountRequestV1 account;

    @Valid
    @NotNull(message = "validation.required")
    private IdRequestV1 role;

    @NotBlank(message = "validation.required")
    private String firstName;

    @NotBlank(message = "validation.required")
    private String middleName;

    @NotBlank(message = "validation.required")
    private String lastName;

    @NotBlank(message = "validation.required")
    @Email(message = "validation.emailAddress.invalid")
    private String emailAddress;

    @NotBlank(message = "validation.required")
    private String countryCode;

    @NotBlank(message = "validation.required")
    @MobileNumber
    private String mobileNumber;

    @NotBlank(message = "validation.required")
    @PreferredUsernameV1
    private String preferredUsername;
}
