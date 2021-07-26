package com.leijendary.spring.iamtemplate.repository;

import com.leijendary.spring.iamtemplate.model.IamRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface IamRoleRepository extends JpaRepository<IamRole, Long>, JpaSpecificationExecutor<IamRole> {
}
