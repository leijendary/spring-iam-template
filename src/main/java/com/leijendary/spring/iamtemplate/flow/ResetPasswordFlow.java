package com.leijendary.spring.iamtemplate.flow;

import com.leijendary.spring.iamtemplate.data.ResetPasswordRequestV1;
import com.leijendary.spring.iamtemplate.data.VerificationData;
import com.leijendary.spring.iamtemplate.data.request.v1.VerifyRequestV1;
import com.leijendary.spring.iamtemplate.data.response.v1.NextCodeV1;
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
public class ResetPasswordFlow {

    private final IamUserCredentialService iamUserCredentialService;
    private final IamVerificationService iamVerificationService;

    @Transactional
    public NextCodeV1 initiateV1(final ResetPasswordRequestV1 request) {
        final var username = request.getUsername();
        final var iamUserCredential = iamUserCredentialService.getByUsername(username);
        final var iamUser = iamUserCredential.getUser();
        final var deviceId = request.getDeviceId();
        final var field = iamUserCredential.getType();
        final var verificationData = new VerificationData();
        verificationData.setDeviceId(deviceId);
        verificationData.setField(field);
        verificationData.setType(RESET_PASSWORD);

        // Create the OTP verification in order for the reset verification API
        // to create a nominate password verification
        iamVerificationService.create(iamUser, verificationData);

        return new NextCodeV1(VERIFICATION, null);
    }

    @Transactional
    public NextCodeV1 verifyV1(final VerifyRequestV1 request) {
        final var verificationData = VerificationDataFactory.of(request);
        verificationData.setType(RESET_PASSWORD);

        var iamVerification = iamVerificationService.verify(verificationData);
        final var iamUser = iamVerification.getUser();
        final var field = iamVerification.getField();

        verificationData.setField(field);
        verificationData.setType(NOMINATE_PASSWORD);

        final var codeGenerationStrategy = new UuidCodeGenerationStrategy();

        iamVerification = iamVerificationService.create(iamUser, verificationData, codeGenerationStrategy);

        // Code to use when nominating a password
        final var code = iamVerification.getCode();

        return new NextCodeV1(NOMINATE_PASSWORD, code);
    }
}
