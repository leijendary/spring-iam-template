package com.leijendary.spring.iamtemplate.repository;

import com.leijendary.spring.iamtemplate.model.IamVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;

public interface IamVerificationRepository extends JpaRepository<IamVerification, Long>,
        JpaSpecificationExecutor<IamVerification> {

    @Modifying
    void deleteAllByUserIdAndType(final long userId, final String type);
}
