package com.leijendary.spring.iamtemplate.data.response.v1;

import lombok.Data;

@Data
public class UserResponseV1 {

    private long id;
    private AccountResponseV1 account;
    private UserRoleResponseV1 role;
    private String firstName;

}
