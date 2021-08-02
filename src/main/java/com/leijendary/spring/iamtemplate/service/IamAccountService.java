package com.leijendary.spring.iamtemplate.service;

import com.leijendary.spring.iamtemplate.model.IamAccount;
import com.leijendary.spring.iamtemplate.repository.IamAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.leijendary.spring.iamtemplate.data.Status.ACTIVE;

@Service
@RequiredArgsConstructor
public class IamAccountService extends AbstractService {

    private final IamAccountRepository iamAccountRepository;

    public IamAccount create(final String type) {
        final var iamAccount = new IamAccount(type, ACTIVE);

        return iamAccountRepository.save(iamAccount);
    }
}
