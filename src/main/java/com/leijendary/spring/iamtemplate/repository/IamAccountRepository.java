package com.leijendary.spring.iamtemplate.repository;

import com.leijendary.spring.iamtemplate.model.IamAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IamAccountRepository extends JpaRepository<IamAccount, Long> {
}
