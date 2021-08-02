package com.leijendary.spring.iamtemplate.flow;

import com.leijendary.spring.iamtemplate.data.ResetPasswordRequestV1;
import com.leijendary.spring.iamtemplate.data.response.v1.VerificationResponseV1;
import com.leijendary.spring.iamtemplate.service.IamUserCredentialService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ResetPasswordFlow {

    private final IamUserCredentialService iamUserCredentialService;

    @Transactional
    public VerificationResponseV1 initiateV1(final ResetPasswordRequestV1 request) {

    }
}
