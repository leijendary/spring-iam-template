package com.leijendary.spring.iamtemplate.flow;

import com.leijendary.spring.iamtemplate.data.request.v1.VerifyRequestV1;
import com.leijendary.spring.iamtemplate.data.response.v1.RegisterVerifyResponseV1;
import com.leijendary.spring.iamtemplate.service.IamVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.leijendary.spring.iamtemplate.data.VerificationType.REGISTRATION;

@Component
@RequiredArgsConstructor
public class RegisterVerifyFlow {

    private final IamVerificationService iamVerificationService;

    public RegisterVerifyResponseV1 verifyV1(final VerifyRequestV1 request) {
        final var iamVerification = iamVerificationService.verify(request, REGISTRATION);

        if (iamVerification == null) {
            return null;
        }

        final var id = iamVerification.getId();
        final var code = iamVerification.getCode();

        return new RegisterVerifyResponseV1(id, code);
    }
}
