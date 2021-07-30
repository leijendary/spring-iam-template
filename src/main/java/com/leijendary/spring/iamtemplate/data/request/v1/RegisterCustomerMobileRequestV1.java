package com.leijendary.spring.iamtemplate.data.request.v1;

import com.leijendary.spring.iamtemplate.validator.annotation.v1.MobileNumber;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class RegisterCustomerMobileRequestV1 {

    @NotBlank(message = "validation.required")
    private String countryCode;

    @NotBlank(message = "validation.required")
    @MobileNumber
    private String mobileNumber;

    @NotBlank(message = "validation.required")
    private String deviceId;
}
