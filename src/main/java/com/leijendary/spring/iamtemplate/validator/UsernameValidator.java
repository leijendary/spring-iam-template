package com.leijendary.spring.iamtemplate.validator;

import com.leijendary.spring.iamtemplate.data.UsernameField;
import com.leijendary.spring.iamtemplate.exception.BlankCountryCodeException;
import com.leijendary.spring.iamtemplate.exception.BlankPreferredUsernameException;
import org.springframework.stereotype.Component;

import static com.leijendary.spring.iamtemplate.data.PreferredUsername.MOBILE_NUMBER;
import static com.leijendary.spring.iamtemplate.util.UsernameUtil.getUsername;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
public class UsernameValidator {

    public void preferredUsername(final UsernameField usernameField, final String preferredUsername) {
        final var username = getUsername(usernameField, preferredUsername);
        final var countryCode = usernameField.getCountryCode();

        // If the preferred username is the mobile number, then the country code should be included
        if (preferredUsername.equals(MOBILE_NUMBER) && isBlank(usernameField.getCountryCode())) {
            throw new BlankCountryCodeException("countryCode");
        }

        // If the username is blank or only equals to the country code, then the value
        // of the preferred username field is blank
        if (username.isBlank() || username.equals(countryCode)) {
            throw new BlankPreferredUsernameException(preferredUsername);
        }
    }
}
