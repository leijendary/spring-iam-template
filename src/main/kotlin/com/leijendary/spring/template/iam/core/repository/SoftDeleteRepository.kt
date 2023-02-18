package com.leijendary.spring.template.iam.core.repository

import com.leijendary.spring.template.iam.core.entity.SoftDeleteEntity

interface SoftDeleteRepository<T : SoftDeleteEntity> {
    fun softDelete(entity: T)
}
