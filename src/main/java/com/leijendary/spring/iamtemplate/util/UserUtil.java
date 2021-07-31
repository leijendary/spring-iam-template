package com.leijendary.spring.iamtemplate.util;

import com.leijendary.spring.iamtemplate.data.UsernameField;
import com.leijendary.spring.iamtemplate.exception.ResourceNotUniqueException;
import com.leijendary.spring.iamtemplate.repository.IamUserRepository;
import com.leijendary.spring.iamtemplate.specification.UserMobileEmailSpecification;

import static com.leijendary.spring.iamtemplate.data.PreferredUsername.EMAIL_ADDRESS;
import static com.leijendary.spring.iamtemplate.data.PreferredUsername.MOBILE_NUMBER;

public class UserUtil {

    public static void checkUniqueness(final UsernameField usernameField, long id,
                                        final IamUserRepository iamUserRepository) {
        // Validate country code and mobile number first
        var specification = UserMobileEmailSpecification.builder()
                .id(id)
                .countryCode(usernameField.getCountryCode())
                .mobileNumber(usernameField.getMobileNumber())
                .build();
        // Get the user based on the country code and mobile number
        var iamUser = iamUserRepository.findOne(specification);

        // If there is an item in the list, throw an error
        if (iamUser.isPresent()) {
            throw new ResourceNotUniqueException(MOBILE_NUMBER,
                    usernameField.getCountryCode() + usernameField.getMobileNumber());
        }

        // Validate email address
        specification = UserMobileEmailSpecification.builder()
                .id(id)
                .emailAddress(usernameField.getEmailAddress())
                .build();
        // Get the user based on the email address
        iamUser = iamUserRepository.findOne(specification);

        // If there is an item in the list, throw an error
        if (iamUser.isPresent()) {
            throw new ResourceNotUniqueException(EMAIL_ADDRESS, usernameField.getEmailAddress());
        }
    }
}
