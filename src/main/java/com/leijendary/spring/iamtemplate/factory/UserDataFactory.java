package com.leijendary.spring.iamtemplate.factory;

import com.leijendary.spring.iamtemplate.data.MobileNumberData;
import com.leijendary.spring.iamtemplate.data.UserData;
import com.leijendary.spring.iamtemplate.data.request.v1.UserRequestV1;

public class UserDataFactory extends AbstractFactory {

    public static UserData of(final UserRequestV1 userRequestV1) {
        return MAPPER.map(userRequestV1, UserData.class);
    }

    public static UserData of(final MobileNumberData mobileNumberData) {
        return MAPPER.map(mobileNumberData, UserData.class);
    }
}
