package com.leijendary.spring.iamtemplate.data;

import com.leijendary.spring.iamtemplate.validator.annotation.v1.MobileNumber;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsernameField {

    @Email(message = "validation.emailAddress.invalid")
    private String emailAddress;

    private String countryCode;

    @MobileNumber
    private String mobileNumber;
}
