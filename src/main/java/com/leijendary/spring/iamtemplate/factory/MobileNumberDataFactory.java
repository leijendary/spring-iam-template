package com.leijendary.spring.iamtemplate.factory;

import com.leijendary.spring.iamtemplate.data.MobileNumberData;
import com.leijendary.spring.iamtemplate.data.UsernameField;
import com.leijendary.spring.iamtemplate.data.request.v1.RegisterCustomerMobileRequestV1;

public class MobileNumberDataFactory extends AbstractFactory {

    public static MobileNumberData of(final UsernameField usernameField) {
        return MAPPER.map(usernameField, MobileNumberData.class);
    }

    public static MobileNumberData of(final RegisterCustomerMobileRequestV1 registerCustomerMobileRequestV1) {
        return MAPPER.map(registerCustomerMobileRequestV1, MobileNumberData.class);
    }
}
