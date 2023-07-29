package com.leijendary.spring.template.iam.api.v1.service

import com.leijendary.spring.template.iam.api.v1.model.LinkResponse
import com.leijendary.spring.template.iam.core.exception.ResourceNotFoundException
import com.leijendary.spring.template.iam.core.storage.S3Storage
import com.leijendary.spring.template.iam.repository.UserRepository
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.s3.model.NoSuchKeyException
import java.util.*

@Service
class PictureService(private val s3Storage: S3Storage, private val userRepository: UserRepository) {
    companion object {
        private const val KEY_SUFFIX = ".png"
        private val source = listOf("profile", "image")
        private val keyPrefix = source.joinToString("/", postfix = "/")
    }

    fun link(id: UUID): LinkResponse {
        val key = createKey(id)
        val link = s3Storage.signTemp(key)

        return LinkResponse(link)
    }

    fun update(id: UUID): LinkResponse {
        val user = userRepository.findCachedByIdOrThrow(id).apply { image = createKey(id) }

        try {
            userRepository.saveAndCache(user)
            s3Storage.moveTemp(user.image!!)
        } catch (_: NoSuchKeyException) {
            throw ResourceNotFoundException(source, id.toString())
        }

        val link = s3Storage.sign(user.image!!)

        return LinkResponse(link)
    }

    private fun createKey(id: UUID) = "$keyPrefix$id$KEY_SUFFIX"
}
