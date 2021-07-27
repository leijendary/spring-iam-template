package com.leijendary.spring.iamtemplate.data.request;


import lombok.Data;

@Data
public class UserQueryRequest {

    private boolean excludeWithAccounts = true;
    private boolean excludeDeactivated = true;
}
