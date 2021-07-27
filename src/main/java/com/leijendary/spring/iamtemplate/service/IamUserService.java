package com.leijendary.spring.iamtemplate.service;

import com.leijendary.spring.iamtemplate.repository.IamUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IamUserService extends AbstractService {

    private final IamUserRepository iamUserRepository;


}
