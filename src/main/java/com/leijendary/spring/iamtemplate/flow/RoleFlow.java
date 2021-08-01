package com.leijendary.spring.iamtemplate.flow;

import com.leijendary.spring.iamtemplate.data.request.QueryRequest;
import com.leijendary.spring.iamtemplate.data.request.v1.RolePermissionRequestV1;
import com.leijendary.spring.iamtemplate.data.request.v1.RoleRequestV1;
import com.leijendary.spring.iamtemplate.data.response.v1.PermissionResponseV1;
import com.leijendary.spring.iamtemplate.data.response.v1.RoleResponseV1;
import com.leijendary.spring.iamtemplate.factory.IamPermissionFactory;
import com.leijendary.spring.iamtemplate.factory.IamRoleFactory;
import com.leijendary.spring.iamtemplate.service.IamRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

import static com.leijendary.spring.iamtemplate.factory.IamRoleFactory.toResponseV1;

@Component
@RequiredArgsConstructor
public class RoleFlow {

    private static final String PAGE_CACHE_V1 = "RoleResponsePageV1";
    private static final String CACHE_V1 = "RoleResponseV1";

    private final IamRoleService roleService;

    @Cacheable(value = PAGE_CACHE_V1, key = "#queryRequest.toString() + '|' + #pageable.toString()")
    public Page<RoleResponseV1> listV1(final QueryRequest queryRequest, final Pageable pageable) {
        final var page = roleService.list(queryRequest, pageable);

        return page.map(IamRoleFactory::toResponseV1);
    }

    @Caching(
            evict = @CacheEvict(value = PAGE_CACHE_V1, allEntries = true),
            put = @CachePut(value = CACHE_V1, key = "#result.id"))
    public RoleResponseV1 createV1(final RoleRequestV1 request) {
        final var iamRole = roleService.create(request);

        return toResponseV1(iamRole);
    }

    @Cacheable(value = CACHE_V1, key = "#id")
    public RoleResponseV1 getV1(final long id) {
        final var iamRole = roleService.get(id);

        return toResponseV1(iamRole);
    }

    @Caching(
            evict = @CacheEvict(value = PAGE_CACHE_V1, allEntries = true),
            put = @CachePut(value = CACHE_V1, key = "#result.id"))
    public RoleResponseV1 updateV1(final long id, final RoleRequestV1 request) {
        final var iamRole = roleService.update(id, request);

        return toResponseV1(iamRole);
    }

    @Caching(evict = {
            @CacheEvict(value = PAGE_CACHE_V1, allEntries = true),
            @CacheEvict(value = CACHE_V1, key = "#id") })
    public void deleteV1(final long id) {
        roleService.delete(id);
    }

    @Cacheable(value = CACHE_V1, key = "#id + '/permissions'")
    public Set<PermissionResponseV1> getPermissionsV1(final long id) {
        return roleService.getPermissions(id)
                .stream()
                .map(IamPermissionFactory::toResponseV1)
                .collect(Collectors.toSet());
    }

    @Caching(
            evict = @CacheEvict(value = CACHE_V1, key = "#id + '/permissions'"),
            put = @CachePut(value = CACHE_V1, key = "#id + '/permissions'"))
    public Set<PermissionResponseV1> addPermissionsV1(final long id, final RolePermissionRequestV1 request) {
        return roleService.addPermissions(id, request).stream()
                .map(IamPermissionFactory::toResponseV1)
                .collect(Collectors.toSet());
    }

    @CacheEvict(value = CACHE_V1, key = "#id + '/permissions'")
    public void removePermissionV1(final long id, final long permissionId) {
        roleService.removePermission(id, permissionId);
    }
}
