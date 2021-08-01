package com.leijendary.spring.iamtemplate.flow;

import com.leijendary.spring.iamtemplate.data.request.v1.NominatePasswordRequestV1;
import com.leijendary.spring.iamtemplate.data.response.v1.RegisterVerifyResponseV1;
import com.leijendary.spring.iamtemplate.service.IamVerificationService;
import com.leijendary.spring.iamtemplate.service.RegisterCustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.leijendary.spring.iamtemplate.data.VerificationType.NOMINATE_PASSWORD;

@Component
@RequiredArgsConstructor
public class NominatePasswordFlow {

    private final RegisterCustomerService registerCustomerService;
    private final IamVerificationService iamVerificationService;

    public RegisterVerifyResponseV1 nominateV1(final NominatePasswordRequestV1 request) {
        final var iamVerification = iamVerificationService.verify(request, NOMINATE_PASSWORD);

        if (iamVerification == null) {
            iamVerification.getUser().getCredentials().stream()
                    .filter(iamUserCredential -> iamUserCredential.getType().equals(iamVerification.getField()))

            return null;
        }

        final var id = iamVerification.getId();
        final var code = iamVerification.getCode();

        return new RegisterVerifyResponseV1(id, code);
    }
}
