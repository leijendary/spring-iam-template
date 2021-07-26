package com.leijendary.spring.iamtemplate.data.response.v1;

import lombok.Data;

import java.util.Set;

@Data
public class RoleResponseV1 {

    private String name;
    private String description;
    private Set<PermissionResponseV1> permissions;
}
