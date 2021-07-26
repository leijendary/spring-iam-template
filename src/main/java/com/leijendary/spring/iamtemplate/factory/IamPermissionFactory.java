package com.leijendary.spring.iamtemplate.factory;

import com.leijendary.spring.iamtemplate.data.request.v1.PermissionRequestV1;
import com.leijendary.spring.iamtemplate.data.response.v1.PermissionResponseV1;
import com.leijendary.spring.iamtemplate.model.IamPermission;

public class IamPermissionFactory extends AbstractFactory {

    public static PermissionResponseV1 toResponseV1(final IamPermission iamPermission) {
        return MAPPER.map(iamPermission, PermissionResponseV1.class);
    }

    public static IamPermission of(final PermissionRequestV1 request) {
        return MAPPER.map(request, IamPermission.class);
    }

    public static void map(final PermissionRequestV1 source, final IamPermission destination) {
        MAPPER.map(source, destination);
    }
}
