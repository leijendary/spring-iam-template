package com.leijendary.spring.iamtemplate.factory;

import com.leijendary.spring.iamtemplate.data.response.v1.PermissionResponseV1;
import com.leijendary.spring.iamtemplate.model.IamPermission;

public class IamPermissionFactory extends AbstractFactory {

    public static PermissionResponseV1 toResponseV1(final IamPermission iamPermission) {
        return MAPPER.map(iamPermission, PermissionResponseV1.class);
    }
}
