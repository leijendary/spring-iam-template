package com.leijendary.spring.iamtemplate.flow;

import com.leijendary.spring.iamtemplate.data.VerificationData;
import com.leijendary.spring.iamtemplate.data.request.v1.RegisterCustomerEmailRequestV1;
import com.leijendary.spring.iamtemplate.data.request.v1.RegisterCustomerFullRequestV1;
import com.leijendary.spring.iamtemplate.data.request.v1.RegisterCustomerMobileRequestV1;
import com.leijendary.spring.iamtemplate.data.response.v1.RegisterResponseV1;
import com.leijendary.spring.iamtemplate.exception.ResourceNotFoundException;
import com.leijendary.spring.iamtemplate.factory.MobileNumberDataFactory;
import com.leijendary.spring.iamtemplate.factory.UserDataFactory;
import com.leijendary.spring.iamtemplate.factory.UsernameFieldFactory;
import com.leijendary.spring.iamtemplate.generator.OtpCodeGenerationStrategy;
import com.leijendary.spring.iamtemplate.model.IamUser;
import com.leijendary.spring.iamtemplate.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.leijendary.spring.iamtemplate.data.AccountType.CUSTOMER;
import static com.leijendary.spring.iamtemplate.data.PreferredUsername.EMAIL_ADDRESS;
import static com.leijendary.spring.iamtemplate.data.PreferredUsername.MOBILE_NUMBER;
import static com.leijendary.spring.iamtemplate.data.VerificationType.REGISTRATION;

@Component
@RequiredArgsConstructor
public class RegisterCustomerFlow {

    private final IamAccountService iamAccountService;
    private final IamRoleService iamRoleService;
    private final IamUserCredentialService iamUserCredentialService;
    private final IamUserService iamUserService;
    private final IamVerificationService iamVerificationService;

    @Transactional
    public RegisterResponseV1 mobileV1(final RegisterCustomerMobileRequestV1 request) {
        final var mobileNumberData = MobileNumberDataFactory.of(request);

        // Check if the mobile number is still unique. Will exclude users
        // with the status of forVerification
        iamUserService.checkUniqueMobileNumber(0, mobileNumberData, true);

        IamUser iamUser = null;

        try {
            // By default, get the existing record
            iamUser = iamUserService.getByMobileNumber(mobileNumberData);
        } catch (ResourceNotFoundException ignored) {}

        // If there is no record yet, create a record
        if (iamUser == null) {
            final var iamAccount = iamAccountService.create(CUSTOMER);
            final var iamRole = iamRoleService.getCustomer();
            final var userData = UserDataFactory.of(mobileNumberData);

            // Create a new user with the default customer account and role
            iamUser = iamUserService.create(userData, iamAccount, iamRole);
        }

        // Username field for updating the credentials
        final var usernameField = UsernameFieldFactory.of(mobileNumberData);

        // Create the credentials of the customer
        if (iamUser.getCredentials().size() > 0) {
            final var credentials = iamUserCredentialService.update(iamUser, usernameField);

            // Set the credentials of the updated user
            iamUser.setCredentials(credentials);
        } else {
            final var credential = iamUserCredentialService.create(
                    iamUser, usernameField, MOBILE_NUMBER);

            iamUser.getCredentials().add(credential);
        }

        final var deviceId = request.getDeviceId();
        // Create the verification data - parameter for creation of a verification code
        final var verificationData = new VerificationData();
        verificationData.setDeviceId(deviceId);
        verificationData.setField(MOBILE_NUMBER);
        verificationData.setType(REGISTRATION);

        // OTP code generation strategy since we want to verify the mobile number
        final var codeGenerationStrategy = new OtpCodeGenerationStrategy();
        // Create a verification code
        final var iamVerification = iamVerificationService.create(
                iamUser, verificationData, codeGenerationStrategy);

        // final var iamUser = registerCustomerService.mobile(request);
        // final var iamVerification = iamVerificationService.create(iamUser, request.getDeviceId(),
        //         MOBILE_NUMBER, REGISTRATION);

        // TODO: build a response with the next step
        // TODO: also build an object with the ID, also another one with ID and code (for email)

        return new RegisterResponseV1(iamVerification.getId());
    }

    @Transactional
    public RegisterResponseV1 emailV1(final RegisterCustomerEmailRequestV1 request) {
        final var iamUser = registerCustomerService.email(request);
        final var iamVerification = iamVerificationService.create(iamUser, request.getDeviceId(),
                EMAIL_ADDRESS, REGISTRATION);

        return new RegisterResponseV1(iamVerification.getId());
    }

    @Transactional
    public RegisterResponseV1 fullV1(final RegisterCustomerFullRequestV1 request) {
        final var iamUser = registerCustomerService.full(request);
        final var iamVerification = iamVerificationService.create(iamUser, request.getDeviceId(),
                request.getPreferredUsername(), REGISTRATION);

        return new RegisterResponseV1(iamVerification.getId());
    }
}
