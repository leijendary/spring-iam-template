package com.leijendary.spring.iamtemplate.data;

import lombok.Data;

@Data
public class UserData {

    private String firstName;
    private String middleName;
    private String lastName;
    private String emailAddress;
    private String countryCode;
    private String mobileNumber;

    public UserData(final String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
