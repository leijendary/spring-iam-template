package com.leijendary.spring.iamtemplate.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsernameField {

    private String emailAddress;
    private String countryCode;
    private String mobileNumber;
}
