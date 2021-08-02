package com.leijendary.spring.iamtemplate.flow;

import com.leijendary.spring.iamtemplate.data.request.QueryRequest;
import com.leijendary.spring.iamtemplate.data.request.UserExclusionRequest;
import com.leijendary.spring.iamtemplate.data.request.v1.AccountRequestV1;
import com.leijendary.spring.iamtemplate.data.request.v1.UserRequestV1;
import com.leijendary.spring.iamtemplate.data.response.v1.UserResponseV1;
import com.leijendary.spring.iamtemplate.factory.IamUserFactory;
import com.leijendary.spring.iamtemplate.factory.UserDataFactory;
import com.leijendary.spring.iamtemplate.factory.UsernameFieldFactory;
import com.leijendary.spring.iamtemplate.service.IamAccountService;
import com.leijendary.spring.iamtemplate.service.IamRoleService;
import com.leijendary.spring.iamtemplate.service.IamUserCredentialService;
import com.leijendary.spring.iamtemplate.service.IamUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.leijendary.spring.iamtemplate.factory.IamUserFactory.toResponseV1;
import static java.util.Optional.ofNullable;

@Component
@RequiredArgsConstructor
public class UserFlow {

    private static final String PAGE_CACHE_V1 = "UserResponsePageV1";
    private static final String CACHE_V1 = "UserResponseV1";

    private final IamAccountService iamAccountService;
    private final IamRoleService iamRoleService;
    private final IamUserCredentialService iamUserCredentialService;
    private final IamUserService iamUserService;

    @Cacheable(value = PAGE_CACHE_V1, key = "#queryRequest.toString() + '|' + #pageable.toString()")
    public Page<UserResponseV1> listV1(final QueryRequest queryRequest, final UserExclusionRequest userQueryRequest,
                                       final Pageable pageable) {
        final var page = iamUserService.list(queryRequest, userQueryRequest, pageable);

        return page.map(IamUserFactory::toResponseV1);
    }

    @Caching(
            evict = @CacheEvict(value = PAGE_CACHE_V1, allEntries = true),
            put = @CachePut(value = CACHE_V1, key = "#result.id"))
    @Transactional
    public UserResponseV1 createV1(final UserRequestV1 request) {
        final var roleId = request.getRole().getId();
        final var iamRole = iamRoleService.get(roleId);
        final var iamAccount = ofNullable(request.getAccount())
                .map(AccountRequestV1::getType)
                .map(iamAccountService::create)
                .orElse(null);
        final var usernameField = UsernameFieldFactory.of(request);

        // Check if the user's email address and mobile numbers are unique.
        // Id is set to 0 since this is user creation
        iamUserService.checkUniqueness(0, usernameField);

        // Convert the request into user data
        final var userData = UserDataFactory.of(request);
        // Save the user's information
        final var iamUser = iamUserService.create(userData, iamAccount, iamRole);
        final var preferredUsername = request.getPreferredUsername();
        // Create the credentials for the user
        final var iamUserCredential = iamUserCredentialService.create(
                iamUser, usernameField, preferredUsername);

        // Add the newly saved credentials to the user's credentials
        iamUser.getCredentials().add(iamUserCredential);

        return toResponseV1(iamUser);
    }

    @Cacheable(value = CACHE_V1, key = "#id")
    public UserResponseV1 getV1(final long id) {
        final var iamUser = iamUserService.get(id);

        return toResponseV1(iamUser);
    }

    @Caching(
            evict = @CacheEvict(value = PAGE_CACHE_V1, allEntries = true),
            put = @CachePut(value = CACHE_V1, key = "#result.id"))
    @Transactional
    public UserResponseV1 updateV1(final long id, final UserRequestV1 request) {
        final var roleId = request.getRole().getId();
        final var iamRole = iamRoleService.get(roleId);
        final var usernameField = UsernameFieldFactory.of(request);

        // Check if the user's email address and mobile numbers are unique.
        // Use the current user's id for uniqueness validation
        iamUserService.checkUniqueness(id, usernameField);

        // Convert the request into user data
        final var userData = UserDataFactory.of(request);
        // Save the user's information
        final var iamUser = iamUserService.update(id, userData, iamRole);
        // Update the credentials of the use
        final var credentials = iamUserCredentialService.update(iamUser, usernameField);

        // Set the newly updated credentials to the user's credentials
        iamUser.setCredentials(credentials);

        return toResponseV1(iamUser);
    }

    @Caching(evict = {
            @CacheEvict(value = PAGE_CACHE_V1, allEntries = true),
            @CacheEvict(value = CACHE_V1, key = "#id") })
    public void deactivateV1(final long id) {
        iamUserService.deactivate(id);
    }
}
