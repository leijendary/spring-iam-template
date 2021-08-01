package com.leijendary.spring.iamtemplate.service;

import com.leijendary.spring.iamtemplate.config.properties.VerificationProperties;
import com.leijendary.spring.iamtemplate.data.request.v1.VerifyRequestV1;
import com.leijendary.spring.iamtemplate.exception.ResourceNotFoundException;
import com.leijendary.spring.iamtemplate.exception.VerificationExpiredException;
import com.leijendary.spring.iamtemplate.model.IamUser;
import com.leijendary.spring.iamtemplate.model.IamVerification;
import com.leijendary.spring.iamtemplate.repository.IamUserRepository;
import com.leijendary.spring.iamtemplate.repository.IamVerificationRepository;
import com.leijendary.spring.iamtemplate.specification.VerificationSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.leijendary.spring.iamtemplate.data.PreferredUsername.MOBILE_NUMBER;
import static com.leijendary.spring.iamtemplate.data.Status.ACTIVE;
import static com.leijendary.spring.iamtemplate.data.VerificationType.NOMINATE_PASSWORD;
import static com.leijendary.spring.iamtemplate.data.VerificationType.REGISTRATION;
import static com.leijendary.spring.iamtemplate.generator.RandomGenerator.otp;
import static com.leijendary.spring.iamtemplate.util.RequestContextUtil.getLocale;
import static com.leijendary.spring.iamtemplate.util.RequestContextUtil.now;
import static java.util.UUID.randomUUID;

@Service
@RequiredArgsConstructor
public class IamVerificationService extends AbstractService {

    private static final String RESOURCE_NAME = "IAM Verification";

    private final IamUserRepository iamUserRepository;
    private final IamVerificationRepository iamVerificationRepository;
    private final MessageSource messageSource;
    private final VerificationProperties verificationProperties;

    @Transactional
    public IamVerification create(final IamUser iamUser, final String deviceId,
                                  final String preferredUsername, final String type) {
        // Remove the old verifications first
        iamVerificationRepository.deleteAllByUserIdAndType(iamUser.getId(), type);

        final var expiry = verificationProperties.getExpiry();
        String code;

        if (preferredUsername.equals(MOBILE_NUMBER) && type.equals(REGISTRATION)) {
            code = otp();
        } else {
            code = randomUUID().toString();
        }

        final var iamVerification = new IamVerification(iamUser, code, expiry, deviceId, preferredUsername, type);

        // Save the verification object into the database.
        // Should fire a notification event
        return iamVerificationRepository.save(iamVerification);
    }

    @Transactional(noRollbackFor = VerificationExpiredException.class)
    public IamVerification verify(final VerifyRequestV1 verifyRequest, final String type) {
        final var specification = VerificationSpecification.builder()
                .id(verifyRequest.getVerificationId())
                .code(verifyRequest.getCode())
                .type(type)
                .build();
        final var iamVerification = iamVerificationRepository.findOne(specification)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, verifyRequest.getVerificationId()));
        final var expiry = iamVerification.getExpiry();

        // The verification has already expired
        if (expiry.isBefore(now())) {
            // Remove the verification
            iamVerificationRepository.delete(iamVerification);

            throw new VerificationExpiredException("verificationId");
        }

        final var deviceId = iamVerification.getDeviceId();
        final var field = iamVerification.getField();

        if (field.equals(MOBILE_NUMBER) && !deviceId.equals(verifyRequest.getDeviceId())) {
            final var message = messageSource.getMessage("access.deviceId.notMatch", new Object[0],
                    getLocale());

            throw new AccessDeniedException(message);
        }

        // Remove the IamVerification record since this is already verified
        iamVerificationRepository.delete(iamVerification);

        if (type.equals(REGISTRATION)) {
            return registration(iamVerification.getUser(), deviceId, field);
        }

        return null;
    }

    /**
     * If the type is for registration, check the password of the user.
     * If the password is not yet set, create a verification for nomination
     *  of password
     *
     * @param iamUser The user to where to attach the verification
     * @param deviceId The device id of the user when this method is executed
     * @param field Field mapping to the user's credential
     * @return {@link IamVerification} the created nominate password verification
     */
    private IamVerification registration(final IamUser iamUser, final String deviceId, final String field) {
        final var hasPassword = iamUser.getCredentials()
                .stream()
                .anyMatch(credential -> credential.getType().equals(field) && credential.getPassword() != null);

        if (hasPassword) {
            iamUser.setStatus(ACTIVE);

            iamUserRepository.save(iamUser);

            return null;
        }

        return create(iamUser, deviceId, field, NOMINATE_PASSWORD);
    }
}
