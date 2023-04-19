package com.leijendary.spring.template.iam.core.repository

import com.leijendary.spring.template.iam.core.entity.UUIDEntity
import com.leijendary.spring.template.iam.core.model.Seek
import com.leijendary.spring.template.iam.core.model.Seekable
import org.springframework.data.jpa.domain.Specification
import org.springframework.transaction.annotation.Transactional
import kotlin.reflect.KClass

interface SeekPaginationRepository<T : UUIDEntity> {
    @Transactional(readOnly = true)
    fun findAll(entity: KClass<T>, seekable: Seekable = Seekable()): Seek<T>

    @Transactional(readOnly = true)
    fun findAll(entity: KClass<T>, specification: Specification<T>?, seekable: Seekable = Seekable()): Seek<T>
}
