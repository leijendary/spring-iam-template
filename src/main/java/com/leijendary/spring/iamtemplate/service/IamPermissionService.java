package com.leijendary.spring.iamtemplate.service;

import com.leijendary.spring.iamtemplate.data.request.QueryRequest;
import com.leijendary.spring.iamtemplate.data.request.v1.PermissionRequestV1;
import com.leijendary.spring.iamtemplate.data.response.v1.PermissionResponseV1;
import com.leijendary.spring.iamtemplate.exception.ResourceNotFoundException;
import com.leijendary.spring.iamtemplate.exception.ResourceNotUniqueException;
import com.leijendary.spring.iamtemplate.factory.IamPermissionFactory;
import com.leijendary.spring.iamtemplate.model.IamPermission;
import com.leijendary.spring.iamtemplate.repository.IamPermissionRepository;
import com.leijendary.spring.iamtemplate.repository.IamRolePermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import static com.leijendary.spring.iamtemplate.factory.IamPermissionFactory.toResponseV1;

@Service
@RequiredArgsConstructor
public class IamPermissionService extends AbstractService {

    private static final String RESOURCE_NAME = "IAM Permission";
    private static final String PAGE_CACHE_V1 = "PermissionResponsePageV1";
    private static final String CACHE_V1 = "PermissionResponseV1";

    private final IamRolePermissionRepository iamRolePermissionRepository;
    private final IamPermissionRepository iamPermissionRepository;

    @Cacheable(value = PAGE_CACHE_V1, key = "#queryRequest.toString() + '|' + #pageable.toString()")
    public Page<PermissionResponseV1> list(final QueryRequest queryRequest, final Pageable pageable) {
        Page<IamPermission> page;

        if (StringUtils.hasText(queryRequest.getQuery())) {
            page = iamPermissionRepository.findAllByPermissionContainingIgnoreCase(queryRequest.getQuery(), pageable);
        } else {
            page = iamPermissionRepository.findAll(pageable);
        }

        return page.map(IamPermissionFactory::toResponseV1);
    }

    @Caching(
            evict = @CacheEvict(value = PAGE_CACHE_V1, allEntries = true),
            put = @CachePut(value = CACHE_V1, key = "#result.id"))
    public PermissionResponseV1 create(final PermissionRequestV1 permissionRequest) {
        final var iamPermission = IamPermissionFactory.of(permissionRequest);

        iamPermissionRepository
                .findFirstByPermissionIgnoreCaseAndIdNot(permissionRequest.getPermission(), 0)
                .ifPresent(permission -> {
                    throw new ResourceNotUniqueException("permission", permission.getPermission());
                });

        iamPermissionRepository.save(iamPermission);

        return toResponseV1(iamPermission);
    }

    @Cacheable(value = CACHE_V1, key = "#id")
    public PermissionResponseV1 get(final long id) {
        return iamPermissionRepository
                .findById(id)
                .map(IamPermissionFactory::toResponseV1)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));
    }

    @Caching(
            evict = @CacheEvict(value = PAGE_CACHE_V1, allEntries = true),
            put = @CachePut(value = CACHE_V1, key = "#result.id"))
    public PermissionResponseV1 update(final long id, final PermissionRequestV1 permissionRequest) {
        var iamPermission = iamPermissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));

        iamPermissionRepository
                .findFirstByPermissionIgnoreCaseAndIdNot(permissionRequest.getPermission(), id)
                .ifPresent(permission -> {
                    throw new ResourceNotUniqueException("permission", permission.getPermission());
                });

        IamPermissionFactory.map(permissionRequest, iamPermission);

        iamPermission = iamPermissionRepository.save(iamPermission);

        return toResponseV1(iamPermission);
    }

    @Caching(evict = {
            @CacheEvict(value = PAGE_CACHE_V1, allEntries = true),
            @CacheEvict(value = CACHE_V1, key = "#id") })
    @Transactional
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
