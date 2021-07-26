package com.leijendary.spring.iamtemplate.repository;

import com.leijendary.spring.iamtemplate.model.IamPermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IamPermissionRepository extends JpaRepository<IamPermission, Long> {

    Page<IamPermission> findAllByPermissionContainingIgnoreCase(final String permission, final Pageable pageable);

    Optional<IamPermission> findFirstByPermissionIgnoreCaseAndIdNot(final String permission, final long id);
}
