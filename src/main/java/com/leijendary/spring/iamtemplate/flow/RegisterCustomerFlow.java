package com.leijendary.spring.iamtemplate.flow;

import com.leijendary.spring.iamtemplate.data.request.v1.RegisterCustomerEmailRequestV1;
import com.leijendary.spring.iamtemplate.data.request.v1.RegisterCustomerFullRequestV1;
import com.leijendary.spring.iamtemplate.data.request.v1.RegisterCustomerMobileRequestV1;
import com.leijendary.spring.iamtemplate.data.response.v1.RegisterResponseV1;
import com.leijendary.spring.iamtemplate.service.IamVerificationService;
import com.leijendary.spring.iamtemplate.service.RegisterCustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.leijendary.spring.iamtemplate.data.PreferredUsername.EMAIL_ADDRESS;
import static com.leijendary.spring.iamtemplate.data.PreferredUsername.MOBILE_NUMBER;
import static com.leijendary.spring.iamtemplate.data.VerificationType.REGISTRATION;

@Component
@RequiredArgsConstructor
public class RegisterCustomerFlow {

    private final IamVerificationService iamVerificationService;
    private final RegisterCustomerService registerCustomerService;

    @Transactional
    public RegisterResponseV1 mobileV1(final RegisterCustomerMobileRequestV1 request) {
        final var iamUser = registerCustomerService.mobile(request);
        final var iamVerification = iamVerificationService.create(iamUser, request.getDeviceId(),
                MOBILE_NUMBER, REGISTRATION);
        // Verification ID needed for the verification process
        final var verificationId = String.valueOf(iamVerification.getId());

        return new RegisterResponseV1(verificationId);
    }

    @Transactional
    public RegisterResponseV1 emailV1(final RegisterCustomerEmailRequestV1 request) {
        final var iamUser = registerCustomerService.email(request);
        final var iamVerification = iamVerificationService.create(iamUser, request.getDeviceId(),
                EMAIL_ADDRESS, REGISTRATION);
        // Verification ID needed for the verification process
        final var verificationId = String.valueOf(iamVerification.getId());

        return new RegisterResponseV1(verificationId);
    }

    @Transactional
    public RegisterResponseV1 fullV1(final RegisterCustomerFullRequestV1 request) {
        final var iamUser = registerCustomerService.full(request);
        final var iamVerification = iamVerificationService.create(iamUser, request.getDeviceId(),
                request.getPreferredUsername(), REGISTRATION);
        // Verification ID needed for the verification process
        final var verificationId = String.valueOf(iamVerification.getId());

        return new RegisterResponseV1(verificationId);
    }
}
