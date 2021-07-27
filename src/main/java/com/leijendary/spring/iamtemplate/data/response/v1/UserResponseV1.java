package com.leijendary.spring.iamtemplate.data.response.v1;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Set;

@Data
public class UserResponseV1 {

    private long id;
    private AccountResponseV1 account;
    private UserRoleResponseV1 role;
    private Set<CredentialResponseV1> credentials;
    private String firstName;
    private String middleName;
    private String lastName;
    private String emailAddress;
    private String countryCode;
    private String mobileNumber;
    private String status;
    private OffsetDateTime createdDate;
    private String createdBy;
    private OffsetDateTime lastModifiedDate;
    private String lastModifiedBy;
    private OffsetDateTime deactivatedDate;
    private String deactivatedBy;
}
