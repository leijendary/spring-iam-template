package com.leijendary.spring.iamtemplate.factory;

import com.leijendary.spring.iamtemplate.data.MobileNumberData;
import com.leijendary.spring.iamtemplate.data.UserData;
import com.leijendary.spring.iamtemplate.data.request.v1.ProfileRequestV1;
import com.leijendary.spring.iamtemplate.data.request.v1.RegisterCustomerFullRequestV1;
import com.leijendary.spring.iamtemplate.data.request.v1.UserRequestV1;
import com.leijendary.spring.iamtemplate.model.IamUser;

public class UserDataFactory extends AbstractFactory {

    public static UserData of(final UserRequestV1 userRequestV1) {
        return MAPPER.map(userRequestV1, UserData.class);
    }

    public static UserData of(final MobileNumberData mobileNumberData) {
        return MAPPER.map(mobileNumberData, UserData.class);
    }

    public static UserData of(final RegisterCustomerFullRequestV1 fullRequestV1) {
        return MAPPER.map(fullRequestV1, UserData.class);
    }

    public static UserData of(final ProfileRequestV1 profileRequestV1) {
        return MAPPER.map(profileRequestV1, UserData.class);
    }

    public static UserData of(final IamUser iamUser) {
        return MAPPER.map(iamUser, UserData.class);
    }

    public static void map(final ProfileRequestV1 profileRequestV1, final UserData userData) {
        MAPPER.map(profileRequestV1, userData);
    }
}
