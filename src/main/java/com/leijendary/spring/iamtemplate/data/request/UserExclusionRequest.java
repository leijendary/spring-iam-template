package com.leijendary.spring.iamtemplate.data.request;


import lombok.Data;

@Data
public class UserExclusionRequest {

    private boolean excludeWithAccounts = false;
    private boolean excludeDeactivated = false;
}
