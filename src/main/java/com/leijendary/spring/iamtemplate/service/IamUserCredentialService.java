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
                                    final String preferredUsername) {
        return create(iamUser, usernameField, preferredUsername, null);
    }

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

    public IamUserCredential getByUsername(final String username) {
        final var specification = Specification
    }

    @Transactional
    public Set<IamUserCredential> update(final IamUser iamUser, final UsernameField usernameField) {
        return update(iamUser, usernameField, null);
    }

    @Transactional
    public Set<IamUserCredential> update(final IamUser iamUser, final UsernameField usernameField,
                                         final String password) {
        iamUser.getCredentials().forEach(credential -> {
            final var type = credential.getType();
            // Get the new value of the current credential type
            final var username = getUsername(usernameField, type);

            // Otherwise, update the value and save the credentials
            credential.setUsername(username);

            // If the password is not blank or there is a value, encode the
            // value and use that as the user's updated password
            if (!isBlank(password)) {
                final var encodedPassword = passwordEncoder.encode(password);

                credential.setPassword(encodedPassword);
            }

            iamUserCredentialRepository.save(credential);
        });

        return iamUser.getCredentials();
    }

    public boolean hasPassword(final IamUser iamUser, final String field) {
        return iamUser.getCredentials()
                .stream()
                .anyMatch(credential -> credential.getType().equals(field) && credential.getPassword() != null);
    }

    public boolean hasCredentialType(final IamUser iamUser, final String field) {
        return iamUser.getCredentials()
                .stream()
                .anyMatch(credential -> credential.getType().equals(field));
    }
}
