package com.leijendary.spring.iamtemplate.repository;

import com.leijendary.spring.iamtemplate.model.IamUserCredential;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IamUserCredentialRepository extends JpaRepository<IamUserCredential, Long> {
}
