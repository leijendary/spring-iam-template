package com.leijendary.spring.iamtemplate.factory;

import com.leijendary.spring.iamtemplate.data.request.v1.UserRequestV1;
import com.leijendary.spring.iamtemplate.data.response.v1.UserResponseV1;
import com.leijendary.spring.iamtemplate.model.IamUser;

public class IamUserFactory extends AbstractFactory {

    public static UserResponseV1 toResponseV1(final IamUser iamUser) {
        return MAPPER.map(iamUser, UserResponseV1.class);
    }

    public static IamUser of(final UserRequestV1 userRequestV1) {
        return MAPPER.map(userRequestV1, IamUser.class);
    }
}
