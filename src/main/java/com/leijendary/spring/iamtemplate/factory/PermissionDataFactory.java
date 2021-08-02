package com.leijendary.spring.iamtemplate.factory;

import com.leijendary.spring.iamtemplate.data.PermissionData;
import com.leijendary.spring.iamtemplate.data.request.v1.PermissionRequestV1;

public class PermissionDataFactory extends AbstractFactory {

    public static PermissionData of(final PermissionRequestV1 permissionRequestV1) {
        return MAPPER.map(permissionRequestV1, PermissionData.class);
    }
}
