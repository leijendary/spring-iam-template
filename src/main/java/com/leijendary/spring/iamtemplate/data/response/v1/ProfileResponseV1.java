package com.leijendary.spring.iamtemplate.data.response.v1;

import lombok.Data;

@Data
public class ProfileResponseV1 {

    private String firstName;
    private String middleName;
    private String lastName;
    private String emailAddress;
    private String countryCode;
    private String mobileNumber;
}
