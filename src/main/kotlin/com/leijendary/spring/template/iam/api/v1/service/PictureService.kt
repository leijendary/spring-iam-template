package com.leijendary.spring.template.iam.api.v1.service

import com.leijendary.spring.template.iam.api.v1.model.LinkResponse
import com.leijendary.spring.template.iam.core.datasource.transactional
import com.leijendary.spring.template.iam.core.storage.S3Storage
import com.leijendary.spring.template.iam.core.storage.S3Storage.Request.PUT
import com.leijendary.spring.template.iam.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class PictureService(private val s3Storage: S3Storage, private val userRepository: UserRepository) {
    companion object {
        private const val KEY_PREFIX = "profile/image/"
        private const val KEY_SUFFIX = ".png"
        private const val KEY_TEMP_SUFFIX = ".tmp$KEY_SUFFIX"
    }

    fun link(id: UUID): LinkResponse {
        val key = createSourceKey(id)
        val link = s3Storage.sign(key, PUT)

        return LinkResponse(link)
    }

    fun update(id: UUID): LinkResponse {
        val sourceKey = createSourceKey(id)
        val destinationKey = createDestinationKey(id)
        val user = userRepository.findByIdOrThrow(id)
        user.image = destinationKey

        transactional {
            userRepository.save(user)

            s3Storage.run {
                copy(sourceKey, destinationKey)
                delete(sourceKey)
            }
        }

        val link = s3Storage.sign(user.image!!)

        return LinkResponse(link)
    }

    private fun createSourceKey(id: UUID) = "$KEY_PREFIX$id$KEY_TEMP_SUFFIX"

    private fun createDestinationKey(id: UUID) = "$KEY_PREFIX$id$KEY_SUFFIX"
}
