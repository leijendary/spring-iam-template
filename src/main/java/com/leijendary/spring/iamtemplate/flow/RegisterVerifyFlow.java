package com.leijendary.spring.iamtemplate.flow;

import com.leijendary.spring.iamtemplate.data.request.v1.VerifyRequestV1;
import com.leijendary.spring.iamtemplate.data.response.v1.VerificationResponseV1;
import com.leijendary.spring.iamtemplate.factory.VerificationDataFactory;
import com.leijendary.spring.iamtemplate.generator.UuidCodeGenerationStrategy;
import com.leijendary.spring.iamtemplate.service.IamUserCredentialService;
import com.leijendary.spring.iamtemplate.service.IamVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.leijendary.spring.iamtemplate.data.VerificationType.*;

@Component
@RequiredArgsConstructor
public class RegisterVerifyFlow {

    private final IamUserCredentialService iamUserCredentialService;
    private final IamVerificationService iamVerificationService;

    @Transactional
    public VerificationResponseV1 verifyV1(final VerifyRequestV1 request) {
        final var verificationData = VerificationDataFactory.of(request);
        verificationData.setType(REGISTRATION);

        var iamVerification = iamVerificationService.verify(verificationData);
        final var iamUser = iamVerification.getUser();
        final var field = iamVerification.getField();
        final var hasPassword = iamUserCredentialService.hasPassword(iamUser, field);

        // If there is no password set, create a nominate password verification
        if (!hasPassword) {
            verificationData.setField(field);
            verificationData.setType(NOMINATE_PASSWORD);

            final var codeGenerationStrategy = new UuidCodeGenerationStrategy();

            iamVerification = iamVerificationService.create(iamUser, verificationData, codeGenerationStrategy);

            // Code to use when nominating a password
            final var code = iamVerification.getCode();

            return new VerificationResponseV1(NOMINATE_PASSWORD, code);
        }

        return new VerificationResponseV1(AUTHENTICATE, null);
    }
}
