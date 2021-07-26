package com.leijendary.spring.iamtemplate.service;

import com.leijendary.spring.iamtemplate.data.request.QueryRequest;
import com.leijendary.spring.iamtemplate.data.request.v1.RoleRequestV1;
import com.leijendary.spring.iamtemplate.data.response.v1.RoleResponseV1;
import com.leijendary.spring.iamtemplate.exception.ResourceNotFoundException;
import com.leijendary.spring.iamtemplate.exception.ResourceNotUniqueException;
import com.leijendary.spring.iamtemplate.factory.IamRoleFactory;
import com.leijendary.spring.iamtemplate.repository.IamRoleRepository;
import com.leijendary.spring.iamtemplate.specification.RoleListSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.leijendary.spring.iamtemplate.factory.IamRoleFactory.toResponseV1;

@Service
@RequiredArgsConstructor
public class IamRoleService extends AbstractService {

    private static final String RESOURCE_NAME = "IAM Role";
    private static final String PAGE_CACHE_V1 = "RoleResponsePageV1";
    private static final String CACHE_V1 = "RoleResponseV1";

    private final IamRoleRepository iamRoleRepository;

    @Cacheable(value = PAGE_CACHE_V1, key = "#queryRequest.toString() + '|' + #pageable.toString()")
    public Page<RoleResponseV1> list(final QueryRequest queryRequest, final Pageable pageable) {
        final var specification = RoleListSpecification.builder()
                .query(queryRequest.getQuery())
                .build();
        final var page = iamRoleRepository.findAll(specification, pageable);

        return page.map(IamRoleFactory::toResponseV1);
    }

    @Caching(
            evict = @CacheEvict(value = PAGE_CACHE_V1, allEntries = true),
            put = @CachePut(value = CACHE_V1, key = "#result.id"))
    @Transactional
    public RoleResponseV1 create(final RoleRequestV1 roleRequest) {
        final var iamRole = IamRoleFactory.of(roleRequest);

        iamRoleRepository
                .findFirstByNameIgnoreCaseAndIdNot(roleRequest.getName(), 0)
                .ifPresent(role -> {
                    throw new ResourceNotUniqueException("name", role.getName());
                });

        iamRoleRepository.save(iamRole);

        return toResponseV1(iamRole);
    }

    @Cacheable(value = CACHE_V1, key = "#id")
    public RoleResponseV1 get(final long id) {
        return iamRoleRepository
                .findById(id)
                .map(IamRoleFactory::toResponseV1)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));
    }

    @Caching(
            evict = @CacheEvict(value = PAGE_CACHE_V1, allEntries = true),
            put = @CachePut(value = CACHE_V1, key = "#result.id"))
    public RoleResponseV1 update(final long id, final RoleRequestV1 roleRequest) {
        var iamRole = iamRoleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));

        iamRoleRepository
                .findFirstByNameIgnoreCaseAndIdNot(roleRequest.getName(), id)
                .ifPresent(role -> {
                    throw new ResourceNotUniqueException("name", role.getName());
                });

        IamRoleFactory.map(roleRequest, iamRole);

        iamRole = iamRoleRepository.save(iamRole);

        return toResponseV1(iamRole);
    }

    @Caching(evict = {
            @CacheEvict(value = PAGE_CACHE_V1, allEntries = true),
            @CacheEvict(value = CACHE_V1, key = "#id") })
    public void delete(final long id) {
        final var iamRole = iamRoleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE_NAME, id));

        iamRoleRepository.delete(iamRole);
    }
}
