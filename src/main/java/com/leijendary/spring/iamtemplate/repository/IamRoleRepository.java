package com.leijendary.spring.iamtemplate.repository;

import com.leijendary.spring.iamtemplate.model.IamRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface IamRoleRepository extends JpaRepository<IamRole, Long>, JpaSpecificationExecutor<IamRole> {

    Optional<IamRole> findFirstByNameIgnoreCaseAndIdNot(final String name, final long id);
}
