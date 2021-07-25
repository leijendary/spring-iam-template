package com.leijendary.spring.iamtemplate.service;

import com.leijendary.spring.iamtemplate.data.request.QueryRequest;
import com.leijendary.spring.iamtemplate.data.request.v1.SampleRequestV1;
import com.leijendary.spring.iamtemplate.data.response.v1.PermissionResponseV1;
import com.leijendary.spring.iamtemplate.factory.IamPermissionFactory;
import com.leijendary.spring.iamtemplate.repository.IamPermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class IamPermissionService extends AbstractService {

    private static final String RESOURCE_NAME = "IAM Permission";
    private static final String PAGE_CACHE_V1 = "PermissionResponsePageV1";
    private static final String CACHE_V1 = "PermissionResponseV1";

    private final IamPermissionRepository iamPermissionRepository;

    @Cacheable(value = PAGE_CACHE_V1, key = "#queryRequest.toString() + '|' + #pageable.toString()")
    public Page<PermissionResponseV1> list(final QueryRequest queryRequest, final Pageable pageable) {
        return iamPermissionRepository.findAllByPermissionContainingIgnoreCase(queryRequest.getQuery(), pageable)
                .map(IamPermissionFactory::toResponseV1);
    }

    @Caching(
            evict = @CacheEvict(value = PAGE_CACHE_V1, allEntries = true),
            put = @CachePut(value = CACHE_V1, key = "#result.id"))
    @Transactional
    public PermissionResponseV1 create(final SampleRequestV1 sampleRequest) {
        final var sampleTable = SampleFactory.of(sampleRequest);

        sampleTableRepository
                .findFirstByColumn1IgnoreCaseAndIdNot(sampleRequest.getField1(), 0)
                .ifPresent(sampleTable1 -> {
                    throw new ResourceNotUniqueException("field1", sampleRequest.getField1());
                });

        sampleTableRepository.save(sampleTable);

        return toResponseV1(sampleTable);
    }
}
