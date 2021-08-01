package com.leijendary.spring.iamtemplate.validator;

import com.leijendary.spring.iamtemplate.data.UsernameField;
import com.leijendary.spring.iamtemplate.exception.ResourceNotUniqueException;
import com.leijendary.spring.iamtemplate.repository.IamUserRepository;
import com.leijendary.spring.iamtemplate.specification.UserMobileEmailSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.leijendary.spring.iamtemplate.data.PreferredUsername.EMAIL_ADDRESS;
import static com.leijendary.spring.iamtemplate.data.PreferredUsername.MOBILE_NUMBER;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final IamUserRepository iamUserRepository;

    public void checkUniqueness(final UsernameField usernameField, long id) {
        checkUniqueMobileNumber(id, usernameField.getCountryCode(), usernameField.getMobileNumber());
        checkUniqueEmailAddress(id, usernameField.getEmailAddress());
    }

    private void checkUniqueMobileNumber(final long id, final String countryCode, final String mobileNumber) {
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

        // If there is an item in the list, throw an error
        if (iamUser.isPresent()) {
            throw new ResourceNotUniqueException(MOBILE_NUMBER, countryCode + mobileNumber);
        }
    }

    private void checkUniqueEmailAddress(final long id, final String emailAddress) {
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

        // If there is an item in the list, throw an error
        if (iamUser.isPresent()) {
            throw new ResourceNotUniqueException(EMAIL_ADDRESS, emailAddress);
        }
    }
}
