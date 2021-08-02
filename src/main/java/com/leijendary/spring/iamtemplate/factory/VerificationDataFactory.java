package com.leijendary.spring.iamtemplate.factory;

import com.leijendary.spring.iamtemplate.data.VerificationData;
import com.leijendary.spring.iamtemplate.data.request.v1.VerifyRequestV1;

public class VerificationDataFactory extends AbstractFactory {

    public static VerificationData of(final VerifyRequestV1 verifyRequestV1) {
        return MAPPER.map(verifyRequestV1, VerificationData.class);
    }
}
