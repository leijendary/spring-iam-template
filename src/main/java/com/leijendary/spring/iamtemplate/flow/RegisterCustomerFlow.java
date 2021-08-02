package com.leijendary.spring.iamtemplate.flow;

import com.leijendary.spring.iamtemplate.data.UserData;
import com.leijendary.spring.iamtemplate.data.VerificationData;
import com.leijendary.spring.iamtemplate.data.request.v1.RegisterCustomerEmailRequestV1;
import com.leijendary.spring.iamtemplate.data.request.v1.RegisterCustomerFullRequestV1;
import com.leijendary.spring.iamtemplate.data.request.v1.RegisterCustomerMobileRequestV1;
import com.leijendary.spring.iamtemplate.data.response.v1.VerificationResponseV1;
import com.leijendary.spring.iamtemplate.exception.ResourceNotFoundException;
import com.leijendary.spring.iamtemplate.factory.MobileNumberDataFactory;
import com.leijendary.spring.iamtemplate.factory.UserDataFactory;
import com.leijendary.spring.iamtemplate.factory.UsernameFieldFactory;
import com.leijendary.spring.iamtemplate.model.IamUser;
import com.leijendary.spring.iamtemplate.service.*;
import com.leijendary.spring.iamtemplate.util.UsernameUtil;
import com.leijendary.spring.iamtemplate.validator.UsernameValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

import static com.leijendary.spring.iamtemplate.data.AccountType.CUSTOMER;
import static com.leijendary.spring.iamtemplate.data.PreferredUsername.EMAIL_ADDRESS;
import static com.leijendary.spring.iamtemplate.data.PreferredUsername.MOBILE_NUMBER;
import static com.leijendary.spring.iamtemplate.data.VerificationType.REGISTRATION;
import static com.leijendary.spring.iamtemplate.data.VerificationType.VERIFICATION;

@Component
@RequiredArgsConstructor
public class RegisterCustomerFlow {

    private final IamAccountService iamAccountService;
    private final IamRoleService iamRoleService;
    private final IamUserCredentialService iamUserCredentialService;
    private final IamUserService iamUserService;
    private final IamVerificationService iamVerificationService;
    private final UsernameValidator usernameValidator;

    @Transactional
    public VerificationResponseV1 mobileV1(final RegisterCustomerMobileRequestV1 request) {
        final var mobileNumberData = MobileNumberDataFactory.of(request);

        // Check if the mobile number is still unique. Will exclude users
        // with the status of forVerification
        iamUserService.checkUniqueMobileNumber(0, mobileNumberData, true);

        final Supplier<IamUser> iamUserSupplier = () -> iamUserService.getByMobileNumber(mobileNumberData);
        final Supplier<UserData> userDataSupplier = () -> UserDataFactory.of(mobileNumberData);
        final var iamUser = userCreationProcess(
                iamUserSupplier, userDataSupplier, MOBILE_NUMBER, null);
        final var deviceId= request.getDeviceId();

        // Send a verification code to the user's mobile number
        verificationProcess(iamUser, deviceId, MOBILE_NUMBER);

        // Since this is a registration via mobile number, the API consumer's
        // next action is to do a verification process
        return new VerificationResponseV1(VERIFICATION, null);
    }

    @Transactional
    public VerificationResponseV1 emailV1(final RegisterCustomerEmailRequestV1 request) {
        final var emailAddress = request.getEmailAddress();

        // Check if the email address is still unique. Will exclude users
        // with the status of forVerification
        iamUserService.checkUniqueEmailAddress(0, emailAddress, true);

        final Supplier<IamUser> iamUserSupplier = () -> iamUserService.getByEmailAddress(emailAddress);
        final Supplier<UserData> userDataSupplier = () -> new UserData(emailAddress);
        final var iamUser = userCreationProcess(
                iamUserSupplier, userDataSupplier, EMAIL_ADDRESS, null);
        final var deviceId= request.getDeviceId();

        // Send a verification code to the user's email address
        verificationProcess(iamUser, deviceId, EMAIL_ADDRESS);

        // Since this is a registration via email address, the API consumer's
        // next action is to do a verification process
        return new VerificationResponseV1(VERIFICATION, null);
    }

    @Transactional
    public VerificationResponseV1 fullV1(final RegisterCustomerFullRequestV1 request) {
        final var preferredUsername = request.getPreferredUsername();
        final var usernameField = UsernameFieldFactory.of(request);

        // Validate the parameters if they have value based on the preferredUsername
        usernameValidator.preferredUsername(usernameField, preferredUsername);

        IamUser tentativeUser;

        try {
            tentativeUser = iamUserService.getByPreferredUsername(usernameField, preferredUsername);
        } catch (final ResourceNotFoundException ignored) {
            tentativeUser = new IamUser();
        }

        // Username based on the preferredUsername field
        final var username = UsernameUtil.getUsername(usernameField, preferredUsername);
        final var status = tentativeUser.getStatus();

        // Throw an error if the user's status is not set to for verification
        // meaning the user is already past the verification stage
        iamUserService.throwIfNotForVerification(status, preferredUsername, username);

        final var id = tentativeUser.getId();

        // Check the uniqueness of the username field and
        iamUserService.checkUniqueness(id, usernameField);

        final var finalIamUser = id == 0 ? null : tentativeUser;
        final Supplier<IamUser> iamUserSupplier = () -> finalIamUser;
        final Supplier<UserData> userDataSupplier = () -> UserDataFactory.of(request);
        final var password = request.getPassword();
        final var iamUser = userCreationProcess(
                iamUserSupplier, userDataSupplier, preferredUsername, password);
        final var deviceId = request.getDeviceId();

        // Send a verification code to the user's preferred username
        verificationProcess(iamUser, deviceId, preferredUsername);

        return new VerificationResponseV1(VERIFICATION, null);
    }

    private IamUser userCreationProcess(final Supplier<IamUser> iamUserSupplier,
                                        final Supplier<UserData> userDataSupplier, final String field,
                                        final String password) {
        IamUser iamUser = null;

        try {
            // By default, get the existing record
            iamUser = iamUserSupplier.get();
        } catch (ResourceNotFoundException ignored) {}

        // If there is no record yet, create a record
        if (iamUser == null) {
            final var iamAccount = iamAccountService.create(CUSTOMER);
            final var iamRole = iamRoleService.getCustomer();
            final var userData = userDataSupplier.get();

            // Create a new user with the default customer account and role
            iamUser = iamUserService.create(userData, iamAccount, iamRole);
        }

        createUpdateCredentials(iamUser, field, password);

        return iamUser;
    }

    private void createUpdateCredentials(final IamUser iamUser, final String field, final String password) {
        // Username field for updating the credentials
        final var usernameField = UsernameFieldFactory.of(iamUser);
        final var hasCredentialType = iamUserCredentialService.hasCredentialType(iamUser, field);

        // Create the credentials of the customer
        if (!hasCredentialType) {
            final var credential = iamUserCredentialService.create(
                    iamUser, usernameField, field, password);

            iamUser.getCredentials().add(credential);
        } else {
            final var credentials = iamUserCredentialService.update(iamUser, usernameField);

            // Set the credentials of the updated user
            iamUser.setCredentials(credentials);
        }
    }

    private void verificationProcess(final IamUser iamUser, final String deviceId, final String field) {
        // Create the verification data - parameter for creation of a verification code
        final var verificationData = new VerificationData();
        verificationData.setDeviceId(deviceId);
        verificationData.setField(field);
        verificationData.setType(REGISTRATION);

        // Create a verification code
        iamVerificationService.create(iamUser, verificationData);
    }
}
