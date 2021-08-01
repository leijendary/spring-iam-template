package com.leijendary.spring.iamtemplate.service;

import com.leijendary.spring.iamtemplate.config.properties.RoleProperties;
import com.leijendary.spring.iamtemplate.config.properties.VerificationProperties;
import com.leijendary.spring.iamtemplate.data.UsernameField;
import com.leijendary.spring.iamtemplate.data.request.v1.RegisterCustomerEmailRequestV1;
import com.leijendary.spring.iamtemplate.data.request.v1.RegisterCustomerFullRequestV1;
import com.leijendary.spring.iamtemplate.data.request.v1.RegisterCustomerMobileRequestV1;
import com.leijendary.spring.iamtemplate.data.response.v1.RegisterResponseV1;
import com.leijendary.spring.iamtemplate.exception.InvalidPreferredUsernameException;
import com.leijendary.spring.iamtemplate.exception.ResourceNotFoundException;
import com.leijendary.spring.iamtemplate.exception.ResourceNotUniqueException;
import com.leijendary.spring.iamtemplate.factory.IamUserFactory;
import com.leijendary.spring.iamtemplate.factory.UsernameFieldFactory;
import com.leijendary.spring.iamtemplate.model.IamAccount;
import com.leijendary.spring.iamtemplate.model.IamUser;
import com.leijendary.spring.iamtemplate.model.IamUserCredential;
import com.leijendary.spring.iamtemplate.model.IamVerification;
import com.leijendary.spring.iamtemplate.repository.*;
import com.leijendary.spring.iamtemplate.specification.UserMobileEmailSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.leijendary.spring.iamtemplate.data.AccountType.CUSTOMER;
import static com.leijendary.spring.iamtemplate.data.PreferredUsername.EMAIL_ADDRESS;
import static com.leijendary.spring.iamtemplate.data.PreferredUsername.MOBILE_NUMBER;
import static com.leijendary.spring.iamtemplate.data.Status.ACTIVE;
import static com.leijendary.spring.iamtemplate.data.Status.FOR_VERIFICATION;
import static com.leijendary.spring.iamtemplate.data.VerificationType.REGISTRATION;
import static com.leijendary.spring.iamtemplate.util.RandomUtil.otp;
import static com.leijendary.spring.iamtemplate.util.UserUtil.checkUniqueness;
import static com.leijendary.spring.iamtemplate.util.UsernameUtil.getUsername;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static java.util.Optional.ofNullable;
import static java.util.UUID.randomUUID;

@Service
@RequiredArgsConstructor
public class RegisterCustomerService extends AbstractService {

    private final IamAccountRepository iamAccountRepository;
    private final IamRoleRepository iamRoleRepository;
    private final IamUserCredentialRepository iamUserCredentialRepository;
    private final IamUserRepository iamUserRepository;
    private final IamVerificationRepository iamVerificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleProperties roleProperties;
    private final VerificationProperties verificationProperties;

    @Transactional
    public RegisterResponseV1 mobile(final RegisterCustomerMobileRequestV1 mobileRequest) {
        final var specification = UserMobileEmailSpecification.builder()
                .countryCode(mobileRequest.getCountryCode())
                .mobileNumber(mobileRequest.getMobileNumber())
                .build();
        final var iamUser = iamUserRepository.findOne(specification)
                .orElseGet(() -> IamUserFactory.of(mobileRequest));
        final var isForVerification = isForVerification(iamUser.getStatus());

        // If the IamUser's status is not equal to for verification,
        // then that user must be already verified and should therefore
        // skip the registration part and move on to the login
        if (!isForVerification) {
            throw new ResourceNotUniqueException(MOBILE_NUMBER,
                    mobileRequest.getCountryCode() + mobileRequest.getMobileNumber());
        }

        // Create the credential
        final var usernameField = UsernameField.builder()
                .countryCode(mobileRequest.getCountryCode())
                .mobileNumber(mobileRequest.getMobileNumber())
                .build();

        // If the IamUser's id is 0, this is a new user.
        if (iamUser.getId() == 0) {
            createUserAndCredential(iamUser, usernameField, MOBILE_NUMBER, null);
        } else {
            createCredential(iamUser, usernameField, MOBILE_NUMBER, null);
        }

        return createVerification(iamUser, mobileRequest.getDeviceId(), MOBILE_NUMBER);
    }

    @Transactional
    public RegisterResponseV1 email(final RegisterCustomerEmailRequestV1 emailRequest) {
        final var specification = UserMobileEmailSpecification.builder()
                .emailAddress(emailRequest.getEmailAddress())
                .build();
        final var iamUser = iamUserRepository.findOne(specification)
                .orElseGet(() -> IamUserFactory.of(emailRequest));
        final var isForVerification = isForVerification(iamUser.getStatus());

        // If the IamUser's status is not equal to for verification,
        // then that user must be already verified and should therefore
        // skip the registration part and move on to the login
        if (!isForVerification) {
            throw new ResourceNotUniqueException(EMAIL_ADDRESS, emailRequest.getEmailAddress());
        }

        // The username to be used
        final var usernameField = UsernameField.builder()
                .emailAddress(emailRequest.getEmailAddress())
                .build();

        // If the IamUser's id is 0, this is a new user.
        if (iamUser.getId() == 0) {
            createUserAndCredential(iamUser, usernameField, EMAIL_ADDRESS, null);
        } else {
            createCredential(iamUser, usernameField, EMAIL_ADDRESS, null);
        }

        return createVerification(iamUser, emailRequest.getDeviceId(), EMAIL_ADDRESS);
    }

    @Transactional
    public RegisterResponseV1 full(final RegisterCustomerFullRequestV1 fullRequest) {
        final var specification = UserMobileEmailSpecification.builder()
                .emailAddress(fullRequest.getEmailAddress())
                .countryCode(fullRequest.getCountryCode())
                .mobileNumber(fullRequest.getMobileNumber())
                .build();
        final var iamUser = iamUserRepository.findOne(specification)
                .orElseGet(() -> IamUserFactory.of(fullRequest));
        final var preferredUsername = fullRequest.getPreferredUsername();
        final var isForVerification = isForVerification(iamUser.getStatus());
        final var usernameField = UsernameFieldFactory.of(fullRequest);
        final var username = getUsername(usernameField, preferredUsername);

        // If the IamUser's status is not equal to for verification,
        // then that user must be already verified and should therefore
        // skip the registration part and move on to the login
        if (!isForVerification) {
            throw new ResourceNotUniqueException(preferredUsername, username);
        }

        checkUniqueness(usernameField, 0, iamUserRepository);

        // If the IamUser's id is 0, this is a new user.
        if (iamUser.getId() == 0) {
            createUserAndCredential(iamUser, usernameField, preferredUsername, fullRequest.getPassword());
        } else {
            createCredential(iamUser, usernameField, preferredUsername, fullRequest.getPassword());
        }

        return createVerification(iamUser, fullRequest.getDeviceId(), preferredUsername);
    }

    private void createUserAndCredential(final IamUser iamUser, final UsernameField usernameField,
                                         final String preferredUsername, final String password) {
        final var roleName = roleProperties.getCustomer().getName();
        // Get the customer's role from the configuration properties
        final var iamRole = iamRoleRepository
                .findFirstByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("role", roleName));
        // Create an account
        final var iamAccount = new IamAccount();
        iamAccount.setType(CUSTOMER);
        iamAccount.setStatus(ACTIVE);

        // Save the IamAccount object
        iamAccountRepository.save(iamAccount);

        // Update the IamUser object attributes
        iamUser.setAccount(iamAccount);
        iamUser.setRole(iamRole);
        iamUser.setStatus(FOR_VERIFICATION);

        // Save the user
        iamUserRepository.save(iamUser);

        createCredential(iamUser, usernameField, preferredUsername, password);
    }

    private void createCredential(final IamUser iamUser, final UsernameField usernameField,
                                  final String preferredUsername, final String password) {
        // Set the credential based on the username from the preferredUsername field
        final var hasCredential = ofNullable(iamUser.getCredentials())
                .orElse(emptySet())
                .stream()
                .anyMatch(credential -> credential.getType().equals(preferredUsername));

        // If there is already a credential based on the preferred username, skip this method
        if (hasCredential) {
            return;
        }

        final var username = getUsername(usernameField, preferredUsername);
        final var iamUserCredential = new IamUserCredential();
        iamUserCredential.setUser(iamUser);
        iamUserCredential.setUsername(username);
        iamUserCredential.setType(preferredUsername);

        // Encode the password into BCrypt and set the password of the credential
        if (password != null) {
            final var encodedPassword = passwordEncoder.encode(password);

            iamUserCredential.setPassword(encodedPassword);
        }

        // Save the credentials
        iamUserCredentialRepository.save(iamUserCredential);

        // Set the user's credentials (no password yet, of course)
        iamUser.setCredentials(singleton(iamUserCredential));
    }

    private RegisterResponseV1 createVerification(final IamUser iamUser, final String deviceId,
                                                  final String preferredUsername) {
        final var type = REGISTRATION;

        // Remove the old verifications first
        iamVerificationRepository.deleteAllByUserIdAndType(iamUser.getId(), type);

        final var expiry = verificationProperties.getExpiry();
        String code;

        if (preferredUsername.equals(MOBILE_NUMBER)) {
            code =  otp();
        } else if (preferredUsername.equals(EMAIL_ADDRESS)) {
            code = randomUUID().toString();
        } else {
            throw new InvalidPreferredUsernameException("preferredUsername");
        }

        final var iamVerification = new IamVerification(iamUser, code, expiry, deviceId, preferredUsername, type);

        // Save the verification object into the database.
        // Should fire a notification event
        iamVerificationRepository.save(iamVerification);

        // Verification ID needed for the verification process
        final var verificationId = String.valueOf(iamVerification.getId());

        return new RegisterResponseV1(verificationId);
    }

    private boolean isForVerification(final String status) {
        return ofNullable(status)
                .map(s -> s.equals(FOR_VERIFICATION))
                .orElse(true);
    }
}
