package com.leijendary.spring.iamtemplate.service;

import com.leijendary.spring.iamtemplate.data.UsernameField;
import com.leijendary.spring.iamtemplate.model.IamUser;
import com.leijendary.spring.iamtemplate.model.IamUserCredential;
import com.leijendary.spring.iamtemplate.repository.IamUserCredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static com.leijendary.spring.iamtemplate.util.UsernameUtil.getUsername;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
@RequiredArgsConstructor
public class IamUserCredentialService extends AbstractService {

    private final IamUserCredentialRepository iamUserCredentialRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public IamUserCredential create(final IamUser iamUser, final UsernameField usernameField,
                                    final String preferredUsername, final String password) {
        final var username = getUsername(usernameField, preferredUsername);
        // Set the credential based on the username from the preferredUsername field
        final var iamUserCredential = new IamUserCredential();
        iamUserCredential.setUser(iamUser);
        iamUserCredential.setUsername(username);
        iamUserCredential.setType(preferredUsername);

        // If the password is not blank or there is a value, encode the
        // value and use that as the user's new password
        if (!isBlank(password)) {
            final var encodedPassword = passwordEncoder.encode(password);

            iamUserCredential.setPassword(encodedPassword);
        }

        return iamUserCredentialRepository.save(iamUserCredential);
    }

    @Transactional
    public Set<IamUserCredential> update(final IamUser iamUser, final UsernameField usernameField) {
        iamUser.getCredentials().forEach(credential -> {
            final var type = credential.getType();
            // Get the new value of the current credential type
            final var username = getUsername(usernameField, type);

            // If the current credential's username is the same value of the current
            // value from the IamUser object, skip this
            if (credential.getUsername().equals(username)) {
                return;
            }

            // Otherwise, update the value and save the credentials
            credential.setUsername(username);

            iamUserCredentialRepository.save(credential);
        });

        return iamUser.getCredentials();
    }

    public boolean hasPassword(final IamUser iamUser, final String field) {
        return iamUser.getCredentials()
                .stream()
                .anyMatch(credential -> credential.getType().equals(field) && credential.getPassword() != null);
    }
}
