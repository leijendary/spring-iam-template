package com.leijendary.spring.iamtemplate.service;

import com.leijendary.spring.iamtemplate.config.properties.VerificationProperties;
import com.leijendary.spring.iamtemplate.exception.InvalidPreferredUsernameException;
import com.leijendary.spring.iamtemplate.model.IamUser;
import com.leijendary.spring.iamtemplate.model.IamVerification;
import com.leijendary.spring.iamtemplate.repository.IamVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.leijendary.spring.iamtemplate.data.PreferredUsername.EMAIL_ADDRESS;
import static com.leijendary.spring.iamtemplate.data.PreferredUsername.MOBILE_NUMBER;
import static com.leijendary.spring.iamtemplate.util.RandomUtil.otp;
import static java.util.UUID.randomUUID;

@Service
@RequiredArgsConstructor
public class IamVerificationService extends AbstractService {

    private static final String RESOURCE_NAME = "IAM Verification";

    private final IamVerificationRepository iamVerificationRepository;
    private final VerificationProperties verificationProperties;

    @Transactional
    public IamVerification create(final IamUser iamUser, final String deviceId,
                                  final String preferredUsername, final String type) {
        // Remove the old verifications first
        iamVerificationRepository.deleteAllByUserIdAndType(iamUser.getId(), type);

        final var expiry = verificationProperties.getExpiry();
        String code;

        if (preferredUsername.equals(MOBILE_NUMBER)) {
            code =  otp();
        } else if (preferredUsername.equals(EMAIL_ADDRESS)) {
            code = randomUUID().toString();
        } else {
            throw new InvalidPreferredUsernameException("preferredUsername");
        }

        final var iamVerification = new IamVerification(iamUser, code, expiry, deviceId, preferredUsername, type);

        // Save the verification object into the database.
        // Should fire a notification event
        return iamVerificationRepository.save(iamVerification);
    }
}
