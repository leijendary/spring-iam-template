package com.leijendary.spring.iamtemplate.data;

import lombok.Data;

import java.util.Set;

@Data
public class RolePermissionData {

    private Set<IdData> permissions;
}
