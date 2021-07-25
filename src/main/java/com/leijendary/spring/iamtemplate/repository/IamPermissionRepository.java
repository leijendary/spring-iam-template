package com.leijendary.spring.iamtemplate.repository;

import com.leijendary.spring.iamtemplate.model.IamPermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IamPermissionRepository extends JpaRepository<IamPermission, Long> {

    Page<IamPermission> findAllByPermissionContainingIgnoreCase(final String permission, final Pageable pageable);
}
