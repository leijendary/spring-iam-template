package com.leijendary.spring.template.iam.api.v1.service

import com.leijendary.spring.template.iam.api.v1.model.LinkResponse
import com.leijendary.spring.template.iam.core.exception.ResourceNotFoundException
import com.leijendary.spring.template.iam.core.storage.S3Storage
import com.leijendary.spring.template.iam.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.*

private const val KEY_SUFFIX = ".png"
private val source = listOf("profile", "image")
private val keyPrefix = source.joinToString("/", postfix = "/")

@Service
class PictureService(private val s3Storage: S3Storage, private val userRepository: UserRepository) {
    fun link(id: UUID): LinkResponse {
        val key = createKey(id)
        val link = s3Storage.signPut(key)

        return LinkResponse(link)
    }

    fun update(id: UUID): LinkResponse {
        val user = userRepository.findCachedByIdOrThrow(id)
        var image = user.image
        var invalidate = true

        if (image === null) {
            image = createKey(id)
            invalidate = false
            val exists = s3Storage.exists(image)

            if (!exists) {
                throw ResourceNotFoundException(source, id.toString())
            }

            user.image = image

            userRepository.saveAndCache(user)
        }

        if (invalidate) {
            s3Storage.invalidateCache(id.toString(), image)
        }

        val link = s3Storage.sign(image)

        return LinkResponse(link)
    }

    private fun createKey(id: UUID) = "$keyPrefix$id$KEY_SUFFIX"
}
