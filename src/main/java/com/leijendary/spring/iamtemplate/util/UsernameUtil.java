package com.leijendary.spring.iamtemplate.util;

import com.leijendary.spring.iamtemplate.data.UsernameField;

import static com.leijendary.spring.iamtemplate.data.PreferredUsername.MOBILE_NUMBER;
import static com.leijendary.spring.iamtemplate.util.ReflectionUtil.getFieldValue;

public class UsernameUtil {

    /**
     * Get the username from the {@link UsernameField} object based on the
     * preferredUsername field set
     *
     * @param usernameField {@link UsernameField}
     * @param preferredUsername What field to use as the username
     * @return {@link String} value of the username from the object. Will return either emailAddress
     * or countryCode + mobileNumber
     */
    public static String getUsername(final UsernameField usernameField, final String preferredUsername) {
        String username;

        try {
            username = (String) getFieldValue(usernameField, preferredUsername);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalArgumentException("Invalid preferredUsername " + preferredUsername, e);
        }

        return preferredUsername.equals(MOBILE_NUMBER) ? usernameField.getCountryCode() + username : username;
    }
}
