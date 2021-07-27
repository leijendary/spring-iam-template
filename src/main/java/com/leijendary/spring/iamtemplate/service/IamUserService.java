package com.leijendary.spring.iamtemplate.service;

import com.leijendary.spring.iamtemplate.data.request.QueryRequest;
import com.leijendary.spring.iamtemplate.data.request.UserQueryRequest;
import com.leijendary.spring.iamtemplate.data.request.v1.UserRequestV1;
import com.leijendary.spring.iamtemplate.data.response.v1.UserResponseV1;
import com.leijendary.spring.iamtemplate.exception.InvalidRoleException;
import com.leijendary.spring.iamtemplate.exception.ResourceNotFoundException;
import com.leijendary.spring.iamtemplate.exception.ResourceNotUniqueException;
import com.leijendary.spring.iamtemplate.factory.IamAccountFactory;
import com.leijendary.spring.iamtemplate.factory.IamUserFactory;
import com.leijendary.spring.iamtemplate.model.IamUserCredential;
import com.leijendary.spring.iamtemplate.repository.IamRoleRepository;
import com.leijendary.spring.iamtemplate.repository.IamUserRepository;
import com.leijendary.spring.iamtemplate.specification.UserListSpecification;
import com.leijendary.spring.iamtemplate.specification.UserUniquenessSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

import static com.leijendary.spring.iamtemplate.data.PreferredUsername.EMAIL_ADDRESS;
import static com.leijendary.spring.iamtemplate.data.PreferredUsername.MOBILE_NUMBER;
import static com.leijendary.spring.iamtemplate.data.Status.ACTIVE;
import static com.leijendary.spring.iamtemplate.util.ReflectionUtil.getFieldValue;
import static com.leijendary.spring.iamtemplate.util.RequestContextUtil.now;

@Service
@RequiredArgsConstructor
public class IamUserService extends AbstractService {

    private static final String RESOURCE_NAME = "IAM User";

    private final IamRoleRepository iamRoleRepository;
    private final IamUserRepository iamUserRepository;

    public Page<UserResponseV1> list(final QueryRequest queryRequest, final UserQueryRequest userQueryRequest,
                                     final Pageable pageable) {
        final var specification = UserListSpecification.builder()
                .query(queryRequest.getQuery())
                .excludeWithAccounts(userQueryRequest.isExcludeWithAccounts())
                .excludeDeactivated(userQueryRequest.isExcludeDeactivated())
                .build();
        final var page = iamUserRepository.findAll(specification, pageable);

        return page.map(IamUserFactory::toResponseV1);
    }

    public UserResponseV1 create(final UserRequestV1 userRequest) {
        final var role = iamRoleRepository.findById(userRequest.getRole().getId())
                .orElseThrow(() -> new InvalidRoleException("role.id", userRequest.getRole().getId()));
        final var account = Optional.ofNullable(userRequest.getAccount())
                .map(accountRequest -> {
                    // Create a new IamAccount
                    final var iamAccount = IamAccountFactory.of(accountRequest);
                    // With the default status of "active"
                    iamAccount.setStatus(ACTIVE);

                    return iamAccount;
                })
                .orElse(null);

        // Validate the uniqueness of the user
        checkUniqueness(userRequest, 0);

        final var username = getUsername(userRequest);
        // Set the credential based on the username from the preferredUsername field
        final var credential = new IamUserCredential();
        credential.setUsername(username);
        credential.setType(userRequest.getPreferredUsername());

        final var iamUser = IamUserFactory.of(userRequest);
        iamUser.setAccount(account);
        iamUser.setRole(role);
        iamUser.setCredentials(Set.of(credential));

        iamUserRepository.save(iamUser);

        return IamUserFactory.toResponseV1(iamUser);
    }

    public UserResponseV1 get(final long id) {
        return iamUserRepository.findById(id)
                // Cannot show a deactivated user
                .filter(user -> user.getDeactivatedDate() == null)
                .map(IamUserFactory::toResponseV1)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));
    }

    public UserResponseV1 update(final long id, final UserRequestV1 userRequest) {
        final var iamUser = iamUserRepository.findById(id)
                // Cannot show a deactivated user
                .filter(user -> user.getDeactivatedDate() == null)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));

        // Validate the uniqueness of the user
        checkUniqueness(userRequest, iamUser.getId());

        // Update the values of the current IamUser object
        IamUserFactory.map(userRequest, iamUser);

        // Update mobile number credential if there is a new value
        updateCredentialUsername(iamUser.getCredentials(), MOBILE_NUMBER,
                iamUser.getCountryCode() + iamUser.getMobileNumber());

        // Update email address credential if there is a new value
        updateCredentialUsername(iamUser.getCredentials(), EMAIL_ADDRESS, iamUser.getEmailAddress());

        return IamUserFactory.toResponseV1(iamUser);
    }

    public void deactivate(final long id) {
        final var iamUser = iamUserRepository.findById(id)
                // Cannot show a deactivated user
                .filter(user -> user.getDeactivatedDate() == null)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));

        iamUser.setDeactivatedDate(now());
    }

    private void updateCredentialUsername(final Set<IamUserCredential> credentials, final String type,
                                          final String newValue) {
        credentials
                .stream()
                .filter(c -> c.getType().equals(type))
                .findFirst()
                .ifPresent((credential) -> {
                    if (!credential.getUsername().equals(newValue)) {
                        credential.setUsername(newValue);
                    }
                });
    }

    private void checkUniqueness(final UserRequestV1 userRequest, long id) {
        final var preferredUsername = userRequest.getPreferredUsername();
        final var builder = UserUniquenessSpecification.builder()
                .id(id);

        // If the preferred username is mobileNumber, just set the countryCode and mobileNumber
        if (preferredUsername.equals(MOBILE_NUMBER)) {
            builder
                    .countryCode(userRequest.getCountryCode())
                    .mobileNumber(userRequest.getMobileNumber());
        } else {
            // Else, insert email
            builder.emailAddress(userRequest.getEmailAddress());
        }

        final var specification = builder.build();
        // Filter only one item
        final var oneItem = Pageable.ofSize(1);
        // Get the list of filtered users based on the specification
        final var page = iamUserRepository.findAll(specification, oneItem);

        // If there is an item in the list, throw an error
        if (page.hasNext()) {
            final var username = getUsername(userRequest);

            throw new ResourceNotUniqueException(preferredUsername, username);
        }
    }

    /**
     * Get the username from the {@link UserRequestV1} object based on the {@link UserRequestV1#preferredUsername}
     * field set
     *
     * @param userRequest {@link UserRequestV1}
     * @return {@link String} value of the username from the object. Will return either emailAddress
     * or countryCode + mobileNumber
     */
    private String getUsername(final UserRequestV1 userRequest) {
        final var preferredUsername = userRequest.getPreferredUsername();
        String username;

        try {
            username = (String) getFieldValue(userRequest, preferredUsername);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalArgumentException("Invalid preferredUsername " + preferredUsername, e);
        }


        return preferredUsername.equals(MOBILE_NUMBER) ? userRequest.getCountryCode() + username : username;
    }
}
