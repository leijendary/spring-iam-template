package com.leijendary.spring.iamtemplate.service;

import com.leijendary.spring.iamtemplate.config.properties.RoleProperties;
import com.leijendary.spring.iamtemplate.config.properties.VerificationProperties;
import com.leijendary.spring.iamtemplate.data.UsernameField;
import com.leijendary.spring.iamtemplate.data.request.v1.RegisterCustomerFullRequestV1;
import com.leijendary.spring.iamtemplate.data.request.v1.RegisterCustomerMobileRequestV1;
import com.leijendary.spring.iamtemplate.data.request.v1.RegisterVerificationResponseV1;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static com.leijendary.spring.iamtemplate.data.AccountType.CUSTOMER;
import static com.leijendary.spring.iamtemplate.data.Auditing.SYSTEM;
import static com.leijendary.spring.iamtemplate.data.PreferredUsername.MOBILE_NUMBER;
import static com.leijendary.spring.iamtemplate.data.Status.ACTIVE;
import static com.leijendary.spring.iamtemplate.data.Status.FOR_VERIFICATION;
import static com.leijendary.spring.iamtemplate.data.VerificationType.REGISTRATION;
import static com.leijendary.spring.iamtemplate.util.UserUtil.checkUniqueness;
import static com.leijendary.spring.iamtemplate.util.UsernameUtil.getUsername;
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
    private final RoleProperties roleProperties;
    private final VerificationProperties verificationProperties;

    @Transactional
    public RegisterVerificationResponseV1 mobile(final RegisterCustomerMobileRequestV1 mobileRequest) {
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

        // If the IamUser's id is 0, this is a new user.
        if (iamUser.getId() == 0) {
            // Create the credential
            final var usernameField = UsernameField.builder()
                    .countryCode(mobileRequest.getCountryCode())
                    .mobileNumber(mobileRequest.getCountryCode())
                    .build();

            createIamUser(iamUser, usernameField, MOBILE_NUMBER);
        }

        return createVerification(iamUser);
    }

    @Transactional
    public RegisterVerificationResponseV1 full(final RegisterCustomerFullRequestV1 fullRequest) {
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
            createIamUser(iamUser, usernameField, preferredUsername);
        }

        return createVerification(iamUser);
    }

    private void createIamUser(final IamUser iamUser, final UsernameField usernameField,
                               final String preferredUsername) {
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
        // System as audit values since there is no session here
        iamUser.setCreatedBy(SYSTEM);
        iamUser.setLastModifiedBy(SYSTEM);

        // Save the user
        iamUserRepository.save(iamUser);

        final var username = getUsername(usernameField, preferredUsername);
        // Set the credential based on the username from the preferredUsername field
        final var iamUserCredential = new IamUserCredential();
        iamUserCredential.setUser(iamUser);
        iamUserCredential.setUsername(username);
        iamUserCredential.setType(preferredUsername);

        // Save the credentials
        iamUserCredentialRepository.save(iamUserCredential);

        // Set the user's credentials (no password yet, of course)
        iamUser.setCredentials(Set.of(iamUserCredential));
    }

    private RegisterVerificationResponseV1 createVerification(final IamUser iamUser) {
        final var expiry = verificationProperties.getExpiry();
        final var code = randomUUID().toString();
        final var iamVerification = new IamVerification(iamUser, code, expiry, MOBILE_NUMBER, REGISTRATION);

        // Save the verification object into the database.
        // Should fire a notification event
        iamVerificationRepository.save(iamVerification);

        return new RegisterVerificationResponseV1(code);
    }

    private boolean isForVerification(final String status) {
        return ofNullable(status)
                .map(s -> s.equals(FOR_VERIFICATION))
                .orElse(true);
    }
}
