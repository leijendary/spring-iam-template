package com.leijendary.spring.iamtemplate.flow;

import com.leijendary.spring.iamtemplate.data.request.QueryRequest;
import com.leijendary.spring.iamtemplate.data.request.v1.PermissionRequestV1;
import com.leijendary.spring.iamtemplate.data.response.v1.PermissionResponseV1;
import com.leijendary.spring.iamtemplate.factory.IamPermissionFactory;
import com.leijendary.spring.iamtemplate.service.IamPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.leijendary.spring.iamtemplate.factory.IamPermissionFactory.toResponseV1;

@Component
@RequiredArgsConstructor
public class PermissionFlow {

    private static final String PAGE_CACHE_V1 = "PermissionResponsePageV1";
    private static final String CACHE_V1 = "PermissionResponseV1";

    private final IamPermissionService permissionService;

    @Cacheable(value = PAGE_CACHE_V1, key = "#queryRequest.toString() + '|' + #pageable.toString()")
    public Page<PermissionResponseV1> listV1(final QueryRequest queryRequest, final Pageable pageable) {
        final var page = permissionService.list(queryRequest, pageable);

        return page.map(IamPermissionFactory::toResponseV1);
    }

    @Caching(
            evict = @CacheEvict(value = PAGE_CACHE_V1, allEntries = true),
            put = @CachePut(value = CACHE_V1, key = "#result.id"))
    public PermissionResponseV1 createV1(final PermissionRequestV1 permissionRequest) {
        final var iamPermission = permissionService.create(permissionRequest);

        return toResponseV1(iamPermission);
    }


    @Cacheable(value = CACHE_V1, key = "#id")
    public PermissionResponseV1 getV1(final long id) {
        final var iamPermission = permissionService.get(id);

        return toResponseV1(iamPermission);
    }

    @Caching(
            evict = @CacheEvict(value = PAGE_CACHE_V1, allEntries = true),
            put = @CachePut(value = CACHE_V1, key = "#result.id"))
    public PermissionResponseV1 updateV1(final long id, final PermissionRequestV1 permissionRequest) {
        final var iamPermission = permissionService.update(id, permissionRequest);

        return toResponseV1(iamPermission);
    }

    @Caching(evict = {
            @CacheEvict(value = PAGE_CACHE_V1, allEntries = true),
            @CacheEvict(value = CACHE_V1, key = "#id") })
    @Transactional
    public void deleteV1(final long id) {
        permissionService.delete(id);
    }
}
