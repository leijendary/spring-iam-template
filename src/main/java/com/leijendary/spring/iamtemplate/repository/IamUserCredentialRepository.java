package com.leijendary.spring.iamtemplate.repository;

import com.leijendary.spring.iamtemplate.model.IamUserCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IamUserCredentialRepository extends JpaRepository<IamUserCredential, Long>,
        JpaSpecificationExecutor<IamUserCredential> {
}
