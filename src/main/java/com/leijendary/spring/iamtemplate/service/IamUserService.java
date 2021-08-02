package com.leijendary.spring.iamtemplate.service;

import com.leijendary.spring.iamtemplate.data.MobileNumberData;
import com.leijendary.spring.iamtemplate.data.UserData;
import com.leijendary.spring.iamtemplate.data.UsernameField;
import com.leijendary.spring.iamtemplate.data.request.QueryRequest;
import com.leijendary.spring.iamtemplate.data.request.UserExclusionRequest;
import com.leijendary.spring.iamtemplate.exception.InvalidPreferredUsernameException;
import com.leijendary.spring.iamtemplate.exception.ResourceNotFoundException;
import com.leijendary.spring.iamtemplate.exception.ResourceNotUniqueException;
import com.leijendary.spring.iamtemplate.factory.IamUserFactory;
import com.leijendary.spring.iamtemplate.factory.MobileNumberDataFactory;
import com.leijendary.spring.iamtemplate.model.IamAccount;
import com.leijendary.spring.iamtemplate.model.IamRole;
import com.leijendary.spring.iamtemplate.model.IamUser;
import com.leijendary.spring.iamtemplate.repository.IamUserRepository;
import com.leijendary.spring.iamtemplate.specification.NonDeactivatedAccountUser;
import com.leijendary.spring.iamtemplate.specification.UserListSpecification;
import com.leijendary.spring.iamtemplate.specification.UserMobileEmailSpecification;
import com.leijendary.spring.iamtemplate.util.UsernameUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.leijendary.spring.iamtemplate.data.PreferredUsername.EMAIL_ADDRESS;
import static com.leijendary.spring.iamtemplate.data.PreferredUsername.MOBILE_NUMBER;
import static com.leijendary.spring.iamtemplate.data.Status.FOR_VERIFICATION;
import static com.leijendary.spring.iamtemplate.util.RequestContextUtil.getUsername;
import static com.leijendary.spring.iamtemplate.util.RequestContextUtil.now;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
@RequiredArgsConstructor
public class IamUserService extends AbstractService {

    private static final String RESOURCE_NAME = "IAM User";

    private final IamUserRepository iamUserRepository;

    public Page<IamUser> list(final QueryRequest queryRequest, final UserExclusionRequest userExclusionRequest,
                              final Pageable pageable) {
        final var specification = UserListSpecification.builder()
                .query(queryRequest.getQuery())
                .excludeWithAccounts(userExclusionRequest.isExcludeWithAccounts())
                .excludeDeactivated(userExclusionRequest.isExcludeDeactivated())
                .build();

        return iamUserRepository.findAll(specification, pageable);
    }

    @Transactional
    public IamUser create(final UserData userData, final IamAccount iamAccount, final IamRole iamRole) {
        final var iamUser = IamUserFactory.of(userData);
        iamUser.setAccount(iamAccount);
        iamUser.setRole(iamRole);
        iamUser.setStatus(FOR_VERIFICATION);

        return iamUserRepository.save(iamUser);
    }

    public IamUser get(final long id) {
        final var specification = NonDeactivatedAccountUser.builder()
                .userId(id)
                .build();

        return iamUserRepository.findOne(specification)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));
    }

    public IamUser getByMobileNumber(final MobileNumberData mobileNumberData) {
        final var countryCode = mobileNumberData.getCountryCode();
        final var mobileNumber = mobileNumberData.getMobileNumber();
        final var specification = UserMobileEmailSpecification.builder()
                .countryCode(countryCode)
                .mobileNumber(mobileNumber)
                .build();

        return iamUserRepository.findOne(specification)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, countryCode + mobileNumber));
    }

    public IamUser getByEmailAddress(final String emailAddress) {
        final var specification = UserMobileEmailSpecification.builder()
                .emailAddress(emailAddress)
                .build();

        return iamUserRepository.findOne(specification)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, emailAddress));
    }

    public IamUser getByPreferredUsername(final UsernameField usernameField, final String preferredUsername) {
        final var builder = UserMobileEmailSpecification.builder();
        UserMobileEmailSpecification specification;

        // Get the user based on the preferredUsername
        if (preferredUsername.equals(MOBILE_NUMBER)) {
            final var countryCode = usernameField.getCountryCode();
            final var mobileNumber = usernameField.getMobileNumber();

            specification = builder
                    .countryCode(countryCode)
                    .mobileNumber(mobileNumber)
                    .build();
        } else if (preferredUsername.equals(EMAIL_ADDRESS)) {
            final var emailAddress = usernameField.getEmailAddress();

            specification = builder.emailAddress(emailAddress).build();
        } else {
            throw new InvalidPreferredUsernameException("preferredUsername");
        }

        final var username = UsernameUtil.getUsername(usernameField, preferredUsername);

        return iamUserRepository.findOne(specification)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, username));
    }

    @Transactional
    public IamUser update(final long id, final UserData userData, final IamRole iamRole) {
        final var iamUser = get(id);
        iamUser.setRole(iamRole);

        // Update the values of the current IamUser object
        IamUserFactory.map(userData, iamUser);

        // Save the updated IamUser object
        return iamUserRepository.save(iamUser);
    }

    @Transactional
    public void deactivate(final long id) {
        final var iamUser = get(id);
        iamUser.setDeactivatedDate(now());
        iamUser.setDeactivatedBy(getUsername());

        iamUserRepository.save(iamUser);
    }

    public void setStatus(final int id, final String status) {
        final var iamUser = get(id);

        setStatus(iamUser, status);
    }

    public void setStatus(final IamUser iamUser, final String status) {
        iamUser.setStatus(status);

        iamUserRepository.save(iamUser);
    }

    public void throwIfNotForVerification(final String status, final String preferredUsername,
                                             final String username) {
        boolean isForVerification = ofNullable(status)
                .map(s -> s.equals(FOR_VERIFICATION))
                .orElse(true);

        // If the IamUser's status is not equal to for verification,
        // then that user must be already verified and should therefore
        // skip the registration part and move on to the login
        if (!isForVerification) {
            throw new ResourceNotUniqueException(preferredUsername, username);
        }
    }

    public void checkUniqueness(long id, final UsernameField usernameField) {
        checkUniqueness(id, usernameField, false);
    }

    public void checkUniqueness(long id, final UsernameField usernameField, final boolean checkIfForVerification) {
        final var mobileNumberData = MobileNumberDataFactory.of(usernameField);
        final var emailAddress = usernameField.getEmailAddress();

        checkUniqueMobileNumber(id, mobileNumberData, checkIfForVerification);
        checkUniqueEmailAddress(id, emailAddress, checkIfForVerification);
    }

    public void checkUniqueMobileNumber(final long id, final MobileNumberData mobileNumberData,
                                        final boolean checkIfForVerification) {
        final var countryCode = mobileNumberData.getCountryCode();
        final var mobileNumber = mobileNumberData.getMobileNumber();

        if (isBlank(countryCode) || isBlank(mobileNumber)) {
            return;
        }

        // Validate country code and mobile number first
        final var specification = UserMobileEmailSpecification.builder()
                .id(id)
                .countryCode(countryCode)
                .mobileNumber(mobileNumber)
                .build();
        // Get the user based on the country code and mobile number
        final var iamUser = iamUserRepository.findOne(specification);

        if (iamUser.isEmpty()) {
            return;
        }

        final var iamUserValue = iamUser.get();

        // If we enabled "checkIfForVerification", this will check the status of the
        // found user. If the status is forVerification, skip throwing an exception
        // and consider this as "unique"
        if (checkIfForVerification && iamUserValue.getStatus().equals(FOR_VERIFICATION)) {
            return;
        }

        throw new ResourceNotUniqueException(MOBILE_NUMBER, countryCode + mobileNumber);
    }

    public void checkUniqueEmailAddress(final long id, final String emailAddress,
                                        final boolean checkIfForVerification) {
        if (isBlank(emailAddress)) {
            return;
        }

        // Validate email address
        final var specification = UserMobileEmailSpecification.builder()
                .id(id)
                .emailAddress(emailAddress)
                .build();
        // Get the user based on the email address
        final var iamUser = iamUserRepository.findOne(specification);

        if (iamUser.isEmpty()) {
            return;
        }

        final var iamUserValue = iamUser.get();

        // If we enabled "checkIfForVerification", this will check the status of the
        // found user. If the status is forVerification, skip throwing an exception
        // and consider this as "unique"
        if (checkIfForVerification && iamUserValue.getStatus().equals(FOR_VERIFICATION)) {
            return;
        }

        throw new ResourceNotUniqueException(EMAIL_ADDRESS, emailAddress);
    }
}
