package com.leijendary.spring.iamtemplate.flow;

import com.leijendary.spring.iamtemplate.data.request.v1.ProfileRequestV1;
import com.leijendary.spring.iamtemplate.data.response.v1.ProfileResponseV1;
import com.leijendary.spring.iamtemplate.factory.IamUserFactory;
import com.leijendary.spring.iamtemplate.factory.UserDataFactory;
import com.leijendary.spring.iamtemplate.service.IamUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.leijendary.spring.iamtemplate.util.RequestContextUtil.getUsername;
import static java.lang.Long.parseLong;

@Component
@RequiredArgsConstructor
public class ProfileFlow {

    private final IamUserService iamUserService;

    public ProfileResponseV1 detailV1() {
        final var username = getUsername();
        final var id = parseLong(username);
        final var iamUser = iamUserService.get(id);

        return IamUserFactory.toProfileV1(iamUser);
    }

    @Transactional
    public ProfileResponseV1 updateV1(final ProfileRequestV1 request) {
        final var username = getUsername();
        final var id = parseLong(username);
        var iamUser = iamUserService.get(id);
        final var iamRole = iamUser.getRole();
        // Original user data
        final var userData = UserDataFactory.of(iamUser);

        // Map the changes from the profile request object to the user data object
        UserDataFactory.map(request, userData);

        iamUser = iamUserService.update(id, userData, iamRole);

        return IamUserFactory.toProfileV1(iamUser);
    }
}
