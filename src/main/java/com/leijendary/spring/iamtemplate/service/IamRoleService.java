package com.leijendary.spring.iamtemplate.service;

import com.leijendary.spring.iamtemplate.config.properties.RoleProperties;
import com.leijendary.spring.iamtemplate.data.RoleData;
import com.leijendary.spring.iamtemplate.data.RolePermissionData;
import com.leijendary.spring.iamtemplate.data.request.QueryRequest;
import com.leijendary.spring.iamtemplate.exception.ResourceNotFoundException;
import com.leijendary.spring.iamtemplate.exception.ResourceNotUniqueException;
import com.leijendary.spring.iamtemplate.factory.IamRoleFactory;
import com.leijendary.spring.iamtemplate.model.IamPermission;
import com.leijendary.spring.iamtemplate.model.IamRole;
import com.leijendary.spring.iamtemplate.repository.IamPermissionRepository;
import com.leijendary.spring.iamtemplate.repository.IamRolePermissionRepository;
import com.leijendary.spring.iamtemplate.repository.IamRoleRepository;
import com.leijendary.spring.iamtemplate.specification.RoleListSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class IamRoleService extends AbstractService {

    public static final String RESOURCE_NAME = "IAM Role";

    private final IamPermissionRepository iamPermissionRepository;
    private final IamRolePermissionRepository iamRolePermissionRepository;
    private final IamRoleRepository iamRoleRepository;
    private final RoleProperties roleProperties;

    public Page<IamRole> list(final QueryRequest queryRequest, final Pageable pageable) {
        final var specification = RoleListSpecification.builder()
                .query(queryRequest.getQuery())
                .build();

        return iamRoleRepository.findAll(specification, pageable);
    }

    public IamRole create(final RoleData roleData) {
        final var iamRole = IamRoleFactory.of(roleData);

        iamRoleRepository
                .findFirstByNameIgnoreCaseAndIdNot(roleData.getName(), 0)
                .ifPresent(role -> {
                    throw new ResourceNotUniqueException("name", role.getName());
                });

        return iamRoleRepository.save(iamRole);
    }

    public IamRole get(final long id) {
        return iamRoleRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));
    }

    public IamRole getCustomer() {
        final var roleName = roleProperties.getCustomer().getName();

        return iamRoleRepository.findFirstByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, roleName));
    }

    public IamRole update(final long id, final RoleData roleData) {
        var iamRole = get(id);

        iamRoleRepository
                .findFirstByNameIgnoreCaseAndIdNot(roleData.getName(), id)
                .ifPresent(role -> {
                    throw new ResourceNotUniqueException("name", role.getName());
                });

        IamRoleFactory.map(roleData, iamRole);

        return iamRoleRepository.save(iamRole);
    }

    public void delete(final long id) {
        final var iamRole = iamRoleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));

        iamRoleRepository.delete(iamRole);
    }

    @Transactional(readOnly = true)
    public Set<IamPermission> getPermissions(final long id) {
        return iamRoleRepository.findById(id)
                .map(IamRole::getPermissions)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));
    }

    @Transactional
    public Set<IamPermission> addPermissions(final long id, final RolePermissionData rolePermissionData) {
        final var iamRole = iamRoleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));
        final var permissions = iamRole.getPermissions();

        rolePermissionData.getPermissions().forEach(permission -> iamPermissionRepository
                .findById(permission.getId())
                .ifPresent(permissions::add));

        iamRoleRepository.save(iamRole);

        return iamRole.getPermissions();
    }

    @Transactional
    public void removePermission(final long id, final long permissionId) {
        iamRoleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));

        iamRolePermissionRepository.deleteAllByRoleIdAndPermissionId(id, permissionId);
    }
}
