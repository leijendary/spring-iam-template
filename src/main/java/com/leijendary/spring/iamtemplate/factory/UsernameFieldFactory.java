package com.leijendary.spring.iamtemplate.factory;

import com.leijendary.spring.iamtemplate.data.UsernameField;
import com.leijendary.spring.iamtemplate.data.request.v1.UserRequestV1;

public class UsernameFieldFactory extends AbstractFactory {

    public static UsernameField of(final UserRequestV1 requestV1) {
        return MAPPER.map(requestV1, UsernameField.class);
    }
}
