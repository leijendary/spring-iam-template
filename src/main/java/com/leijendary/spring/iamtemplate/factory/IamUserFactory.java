package com.leijendary.spring.iamtemplate.factory;

import com.leijendary.spring.iamtemplate.data.request.v1.RegisterCustomerEmailRequestV1;
import com.leijendary.spring.iamtemplate.data.request.v1.RegisterCustomerFullRequestV1;
import com.leijendary.spring.iamtemplate.data.request.v1.RegisterCustomerMobileRequestV1;
import com.leijendary.spring.iamtemplate.data.request.v1.UserRequestV1;
import com.leijendary.spring.iamtemplate.data.response.v1.UserResponseV1;
import com.leijendary.spring.iamtemplate.event.schema.UserSchema;
import com.leijendary.spring.iamtemplate.model.IamUser;

public class IamUserFactory extends AbstractFactory {

    public static UserResponseV1 toResponseV1(final IamUser iamUser) {
        return MAPPER.map(iamUser, UserResponseV1.class);
    }

    public static IamUser of(final UserRequestV1 userRequestV1) {
        return MAPPER.map(userRequestV1, IamUser.class);
    }

    public static IamUser of(final RegisterCustomerMobileRequestV1 mobileRequest) {
        return MAPPER.map(mobileRequest, IamUser.class);
    }

    public static IamUser of(final RegisterCustomerEmailRequestV1 emailRequest) {
        return MAPPER.map(emailRequest, IamUser.class);
    }

    public static IamUser of(final RegisterCustomerFullRequestV1 fullRequest) {
        return MAPPER.map(fullRequest, IamUser.class);
    }

    public static void map(final UserRequestV1 userRequestV1, final IamUser iamUser) {
        MAPPER.map(userRequestV1, iamUser);
    }

    public static void map(final RegisterCustomerFullRequestV1 fullRequest, final IamUser iamUser) {
        MAPPER.map(fullRequest, iamUser);
    }

    public static UserSchema toSchema(final IamUser iamUser) {
        return MAPPER.map(iamUser, UserSchema.class);
    }
}
