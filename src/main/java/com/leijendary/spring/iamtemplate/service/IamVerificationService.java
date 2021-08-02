package com.leijendary.spring.iamtemplate.service;

import com.leijendary.spring.iamtemplate.config.properties.VerificationProperties;
import com.leijendary.spring.iamtemplate.data.VerificationData;
import com.leijendary.spring.iamtemplate.exception.ResourceNotFoundException;
import com.leijendary.spring.iamtemplate.exception.VerificationExpiredException;
import com.leijendary.spring.iamtemplate.generator.CodeGenerationStrategy;
import com.leijendary.spring.iamtemplate.generator.OtpCodeGenerationStrategy;
import com.leijendary.spring.iamtemplate.generator.UuidCodeGenerationStrategy;
import com.leijendary.spring.iamtemplate.model.IamUser;
import com.leijendary.spring.iamtemplate.model.IamVerification;
import com.leijendary.spring.iamtemplate.repository.IamVerificationRepository;
import com.leijendary.spring.iamtemplate.specification.VerificationSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.leijendary.spring.iamtemplate.data.PreferredUsername.MOBILE_NUMBER;
import static com.leijendary.spring.iamtemplate.util.RequestContextUtil.getLocale;
import static com.leijendary.spring.iamtemplate.util.RequestContextUtil.now;
import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
public class IamVerificationService extends AbstractService {

    private static final String RESOURCE_NAME = "IAM Verification";

    private final IamVerificationRepository iamVerificationRepository;
    private final MessageSource messageSource;
    private final VerificationProperties verificationProperties;

    @Transactional
    public IamVerification create(final IamUser iamUser, final VerificationData verificationData) {
        final var deviceId = verificationData.getDeviceId();
        final var field = verificationData.getField();
        final var type = verificationData.getType();

        // Remove the old verifications first
        iamVerificationRepository.deleteAllByUserIdAndType(iamUser.getId(), type);

        final var expiry = verificationProperties.getExpiry();
        final var codeGenerationStrategy = codeGenerationStrategy(field);
        final var code = codeGenerationStrategy.generate();
        final var iamVerification = new IamVerification(iamUser, code, expiry, deviceId, field, type);

        // Save the verification object into the database.
        // Should fire a notification event
        return iamVerificationRepository.save(iamVerification);
    }

    public IamVerification get(final VerificationSpecification specification) {
        return iamVerificationRepository.findOne(specification)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, specification.getId()));
    }

    @Transactional(noRollbackFor = VerificationExpiredException.class)
    public IamVerification verify(final VerificationData verificationData) {
        final var verificationId = verificationData.getVerificationId();
        final var deviceId = verificationData.getDeviceId();
        final var code = verificationData.getCode();
        final var type = verificationData.getType();
        // Filter the IamVerification object using the verification ID, code, and type
        final var specification = VerificationSpecification.builder()
                .id(verificationId)
                .code(code)
                .type(type)
                .build();
        final var iamVerification = get(specification);
        final var expiry = iamVerification.getExpiry();

        // The verification has already expired
        if (expiry.isBefore(now())) {
            // Remove the verification
            iamVerificationRepository.delete(iamVerification);

            throw new VerificationExpiredException("verificationId");
        }

        final var field = ofNullable(iamVerification.getField()).orElse("");

        if (field.equals(MOBILE_NUMBER)) {
            final var isSameDeviceId = deviceId.equals(iamVerification.getDeviceId());

            // Verify if the current request's device ID is the same device ID
            // from the verification record
            if (!isSameDeviceId) {
                final var message = messageSource.getMessage("access.deviceId.notMatch", new Object[0],
                        getLocale());

                throw new AccessDeniedException(message);
            }
        }

        // Remove the IamVerification record since this is already verified
        iamVerificationRepository.delete(iamVerification);

        return iamVerification;
    }

    public CodeGenerationStrategy codeGenerationStrategy(String field) {
        field = ofNullable(field).orElse("");

        if (field.equals(MOBILE_NUMBER)) {
            // OTP code generation strategy since we want to verify the mobile number
            return new OtpCodeGenerationStrategy();
        }

        return new UuidCodeGenerationStrategy();
    }
}
