package com.leijendary.spring.iamtemplate.factory;

import com.leijendary.spring.iamtemplate.data.request.v1.RoleRequestV1;
import com.leijendary.spring.iamtemplate.data.response.v1.RoleResponseV1;
import com.leijendary.spring.iamtemplate.model.IamRole;

public class IamRoleFactory extends AbstractFactory {

    public static RoleResponseV1 toResponseV1(final IamRole role) {
        return MAPPER.map(role, RoleResponseV1.class);
    }

    public static IamRole of(final RoleRequestV1 requestV1) {
        return MAPPER.map(requestV1, IamRole.class);
    }

    public static void map(final RoleRequestV1 requestV1, final IamRole iamRole) {
        MAPPER.map(requestV1, iamRole);
    }
}
