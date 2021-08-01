package com.leijendary.spring.iamtemplate.data.request.v1;

import com.leijendary.spring.iamtemplate.validator.annotation.v1.MobileNumber;
import com.leijendary.spring.iamtemplate.validator.annotation.v1.PreferredUsernameV1;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class RegisterCustomerFullRequestV1 {

    @NotBlank(message = "validation.required")
    private String firstName;

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
    private String deviceId;

    @NotBlank(message = "validation.required")
    @PreferredUsernameV1
    private String preferredUsername;

    @NotBlank(message = "validation.required")
    @Digits(message = "validation.digits.invalid", integer = 999999, fraction = 0)
    @Length(message = "validation.digits.exact", min = 6, max = 6)
    private String password;
}
