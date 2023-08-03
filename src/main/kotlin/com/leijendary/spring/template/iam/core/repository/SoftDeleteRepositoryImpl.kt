package com.leijendary.spring.template.iam.core.repository

import com.leijendary.spring.template.iam.core.entity.SoftDeleteEntity
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.data.auditing.DateTimeProvider
import org.springframework.data.domain.AuditorAware
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime

@Repository
class SoftDeleteRepositoryImpl<T : SoftDeleteEntity>(
    private val auditorAware: AuditorAware<String>,
    private val dateTimeProvider: DateTimeProvider,

    @PersistenceContext
    private val entityManager: EntityManager
) : SoftDeleteRepository<T> {
    override fun softDelete(entity: T) {
        entity.apply {
            deletedAt = dateTimeProvider.now.get() as OffsetDateTime
            deletedBy = auditorAware.currentAuditor.get()
        }

        entityManager.merge(entity)
    }
}
