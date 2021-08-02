package com.leijendary.spring.iamtemplate.factory;

import com.leijendary.spring.iamtemplate.data.RoleData;
import com.leijendary.spring.iamtemplate.data.request.v1.RoleRequestV1;

public class RoleDataFactory extends AbstractFactory {

    public static RoleData of(final RoleRequestV1 roleRequestV1) {
        return MAPPER.map(roleRequestV1, RoleData.class);
    }
}
