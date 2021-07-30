package com.leijendary.spring.iamtemplate.service;

import com.leijendary.spring.iamtemplate.data.request.v1.RegisterCustomerMobileRequestV1;
import com.leijendary.spring.iamtemplate.exception.ResourceNotUniqueException;
import com.leijendary.spring.iamtemplate.repository.IamUserRepository;
import com.leijendary.spring.iamtemplate.specification.UserMobileEmailSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.leijendary.spring.iamtemplate.data.PreferredUsername.MOBILE_NUMBER;

@Service
@RequiredArgsConstructor
public class RegisterCustomerService extends AbstractService {

    private static final String RESOURCE_NAME = "IAM User";

    private final IamUserRepository iamUserRepository;

    @Transactional
    public RegisterCustomerResponseV1 mobile(final RegisterCustomerMobileRequestV1 mobileRequest) {
        final var specification = UserMobileEmailSpecification.builder()
                .countryCode(mobileRequest.getCountryCode())
                .mobileNumber(mobileRequest.getMobileNumber())
                .build();
        final var iamUser = iamUserRepository.findOne(specification);

        // Check if the mobile number is unique
        checkMobileUniqueness(mobileRequest);
    }

    private void checkMobileUniqueness(final RegisterCustomerMobileRequestV1 mobileRequest) {

        // If there is an item in the list, throw an error
        if (iamUser.isPresent()) {
            throw new ResourceNotUniqueException(MOBILE_NUMBER,
                    mobileRequest.getCountryCode() + mobileRequest.getMobileNumber());
        }
    }
}
