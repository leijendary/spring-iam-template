package com.leijendary.spring.iamtemplate.factory;

import com.leijendary.spring.iamtemplate.data.UsernameField;
import com.leijendary.spring.iamtemplate.data.request.v1.RegisterCustomerFullRequestV1;
import com.leijendary.spring.iamtemplate.data.request.v1.UserRequestV1;
import com.leijendary.spring.iamtemplate.model.IamUser;

public class UsernameFieldFactory extends AbstractFactory {

    public static UsernameField of(final UserRequestV1 requestV1) {
        return MAPPER.map(requestV1, UsernameField.class);
    }

    public static UsernameField of(final RegisterCustomerFullRequestV1 requestV1) {
        return MAPPER.map(requestV1, UsernameField.class);
    }

    public static UsernameField of(final IamUser iamUser) {
        return MAPPER.map(iamUser, UsernameField.class);
    }
}
