package com.leijendary.spring.iamtemplate.repository;

import com.leijendary.spring.iamtemplate.model.IamRolePermission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IamRolePermissionRepository extends JpaRepository<IamRolePermission, Long> {

    void deleteAllByRoleIdAndPermissionId(final long roleId, final long permissionId);
}
