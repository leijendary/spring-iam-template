package com.leijendary.spring.iamtemplate.flow;

import com.leijendary.spring.iamtemplate.data.request.v1.NominatePasswordRequestV1;
import com.leijendary.spring.iamtemplate.data.response.v1.VerificationResponseV1;
import com.leijendary.spring.iamtemplate.factory.UsernameFieldFactory;
import com.leijendary.spring.iamtemplate.factory.VerificationDataFactory;
import com.leijendary.spring.iamtemplate.service.IamUserCredentialService;
import com.leijendary.spring.iamtemplate.service.IamVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.leijendary.spring.iamtemplate.data.VerificationType.AUTHENTICATE;
import static com.leijendary.spring.iamtemplate.data.VerificationType.NOMINATE_PASSWORD;

@Component
@RequiredArgsConstructor
public class NominatePasswordFlow {

    private final IamUserCredentialService iamUserCredentialService;
    private final IamVerificationService iamVerificationService;

    public VerificationResponseV1 nominateV1(final NominatePasswordRequestV1 request) {
        final var verificationData = VerificationDataFactory.of(request);
        verificationData.setType(NOMINATE_PASSWORD);

        final var iamVerification = iamVerificationService.verify(verificationData);
        final var iamUser = iamVerification.getUser();
        final var field = iamVerification.getField();
        final var password = request.getPassword();
        final var hasPassword = iamUserCredentialService.hasPassword(iamUser, field);
        final var usernameField = UsernameFieldFactory.of(iamUser);

        if (!hasPassword) {
            iamUserCredentialService.create(iamUser, usernameField, field, password);
        } else {
            iamUserCredentialService.update(iamUser, usernameField);
        }

        return new VerificationResponseV1(0, AUTHENTICATE);
    }
}
