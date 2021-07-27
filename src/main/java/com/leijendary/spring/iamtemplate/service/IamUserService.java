package com.leijendary.spring.iamtemplate.service;

import com.leijendary.spring.iamtemplate.data.request.QueryRequest;
import com.leijendary.spring.iamtemplate.data.request.UserQueryRequest;
import com.leijendary.spring.iamtemplate.data.request.v1.UserRequestV1;
import com.leijendary.spring.iamtemplate.data.response.v1.UserResponseV1;
import com.leijendary.spring.iamtemplate.exception.ResourceNotFoundException;
import com.leijendary.spring.iamtemplate.factory.IamAccountFactory;
import com.leijendary.spring.iamtemplate.factory.IamUserFactory;
import com.leijendary.spring.iamtemplate.model.IamUserCredential;
import com.leijendary.spring.iamtemplate.repository.IamAccountRepository;
import com.leijendary.spring.iamtemplate.repository.IamRoleRepository;
import com.leijendary.spring.iamtemplate.repository.IamUserRepository;
import com.leijendary.spring.iamtemplate.specification.UserListSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

import static com.leijendary.spring.iamtemplate.util.ReflectionUtil.getFieldValue;

@Service
@RequiredArgsConstructor
public class IamUserService extends AbstractService {

    private final IamAccountRepository iamAccountRepository;
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
                .orElseThrow(() -> new ResourceNotFoundException(IamRoleService.RESOURCE_NAME, "role.id"));
        final var account = Optional.ofNullable(userRequest.getAccount())
                .map(IamAccountFactory::of)

        String username;

        try {
            username = (String) getFieldValue(userRequest, userRequest.getPreferredUsername());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalArgumentException("Invalid preferredUsername " + userRequest.getPreferredUsername(), e);
        }

        final var credential = new IamUserCredential();
        credential.setUsername(username);

        final var iamUser = IamUserFactory.of(userRequest);
        iamUser.setCredentials(Set.of(credential));

        iamUserRepository.save(iamUser);

        return IamUserFactory.toResponseV1(iamUser);
    }
}
