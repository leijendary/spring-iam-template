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

    @Email(message = "validation.emailAddress.invalid")
    private String emailAddress;

    private String countryCode;

    @MobileNumber
    private String mobileNumber;

    @NotBlank(message = "validation.required")
    private String deviceId;

    @NotBlank(message = "validation.required")
    @PreferredUsernameV1
    private String preferredUsername;

    @NotBlank(message = "validation.required")
    @Digits(message = "validation.digits.invalid", integer = 9999, fraction = 0)
    @Length(message = "validation.digits.exact", min = 4, max = 4)
    private String password;
}
