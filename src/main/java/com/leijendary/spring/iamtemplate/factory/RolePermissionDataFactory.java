package com.leijendary.spring.iamtemplate.factory;

import com.leijendary.spring.iamtemplate.data.RolePermissionData;
import com.leijendary.spring.iamtemplate.data.request.v1.RolePermissionRequestV1;

public class RolePermissionDataFactory extends AbstractFactory {

    public static RolePermissionData of(final RolePermissionRequestV1 rolePermissionRequestV1) {
        return MAPPER.map(rolePermissionRequestV1, RolePermissionData.class);
    }
}
