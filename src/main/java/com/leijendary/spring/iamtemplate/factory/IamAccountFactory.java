package com.leijendary.spring.iamtemplate.factory;

import com.leijendary.spring.iamtemplate.data.request.v1.AccountRequestV1;
import com.leijendary.spring.iamtemplate.model.IamAccount;

public class IamAccountFactory extends AbstractFactory {

    public static IamAccount of(final AccountRequestV1 accountRequest) {
        return MAPPER.map(accountRequest, IamAccount.class);
    }
}
