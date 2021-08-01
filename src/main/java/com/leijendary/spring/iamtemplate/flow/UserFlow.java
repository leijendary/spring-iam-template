package com.leijendary.spring.iamtemplate.flow;

import com.leijendary.spring.iamtemplate.data.request.QueryRequest;
import com.leijendary.spring.iamtemplate.data.request.UserQueryRequest;
import com.leijendary.spring.iamtemplate.data.request.v1.UserRequestV1;
import com.leijendary.spring.iamtemplate.data.response.v1.UserResponseV1;
import com.leijendary.spring.iamtemplate.factory.IamUserFactory;
import com.leijendary.spring.iamtemplate.service.IamUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import static com.leijendary.spring.iamtemplate.factory.IamUserFactory.toResponseV1;

@Component
@RequiredArgsConstructor
public class UserFlow {

    private final IamUserService userService;

    public Page<UserResponseV1> listV1(final QueryRequest queryRequest, final UserQueryRequest userQueryRequest,
                                       final Pageable pageable) {
        final var page = userService.list(queryRequest, userQueryRequest, pageable);

        return page.map(IamUserFactory::toResponseV1);
    }

    public UserResponseV1 createV1(final UserRequestV1 request) {
        final var iamUser = userService.create(request);

        return toResponseV1(iamUser);
    }

    public UserResponseV1 getV1(final long id) {
        final var iamUser = userService.get(id);

        return toResponseV1(iamUser);
    }

    public UserResponseV1 updateV1(final long id, final UserRequestV1 request) {
        final var iamUser = userService.update(id, request);

        return toResponseV1(iamUser);
    }

    public void deactivateV1(final long id) {
        userService.deactivate(id);
    }
}
