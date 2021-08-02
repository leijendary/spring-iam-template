package com.leijendary.spring.iamtemplate.service;

import com.leijendary.spring.iamtemplate.data.PermissionData;
import com.leijendary.spring.iamtemplate.data.request.QueryRequest;
import com.leijendary.spring.iamtemplate.exception.ResourceNotFoundException;
import com.leijendary.spring.iamtemplate.exception.ResourceNotUniqueException;
import com.leijendary.spring.iamtemplate.factory.IamPermissionFactory;
import com.leijendary.spring.iamtemplate.model.IamPermission;
import com.leijendary.spring.iamtemplate.repository.IamPermissionRepository;
import com.leijendary.spring.iamtemplate.repository.IamRolePermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class IamPermissionService extends AbstractService {

    private static final String RESOURCE_NAME = "IAM Permission";

    private final IamRolePermissionRepository iamRolePermissionRepository;
    private final IamPermissionRepository iamPermissionRepository;

    public Page<IamPermission> list(final QueryRequest queryRequest, final Pageable pageable) {
        Page<IamPermission> page;

        if (StringUtils.hasText(queryRequest.getQuery())) {
            page = iamPermissionRepository.findAllByPermissionContainingIgnoreCase(queryRequest.getQuery(), pageable);
        } else {
            page = iamPermissionRepository.findAll(pageable);
        }

        return page;
    }

    public IamPermission create(final PermissionData permissionRequest) {
        final var iamPermission = IamPermissionFactory.of(permissionRequest);

        iamPermissionRepository
                .findFirstByPermissionIgnoreCaseAndIdNot(permissionRequest.getPermission(), 0)
                .ifPresent(permission -> {
                    throw new ResourceNotUniqueException("permission", permission.getPermission());
                });

        return iamPermissionRepository.save(iamPermission);
    }

    public IamPermission get(final long id) {
        return iamPermissionRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));
    }

    public IamPermission update(final long id, final PermissionData permissionRequest) {
        var iamPermission = get(id);

        iamPermissionRepository
                .findFirstByPermissionIgnoreCaseAndIdNot(permissionRequest.getPermission(), id)
                .ifPresent(permission -> {
                    throw new ResourceNotUniqueException("permission", permission.getPermission());
                });

        IamPermissionFactory.map(permissionRequest, iamPermission);

        return iamPermissionRepository.save(iamPermission);
    }

    public void delete(final long id) {
        final var iamPermission = iamPermissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));

        // Delete all iam_role_permission first since they are connected to the role
        iamRolePermissionRepository.deleteAllByPermissionId(id);

        // Once the in used permission is deleted form iam_role_permission,
        // proceed with the deletion of the actual permission
        iamPermissionRepository.delete(iamPermission);
    }
}
