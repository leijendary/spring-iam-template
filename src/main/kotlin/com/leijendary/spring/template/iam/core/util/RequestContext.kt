package com.leijendary.spring.template.iam.core.util

import com.leijendary.spring.template.iam.core.config.HEADER_USER_ID
import com.leijendary.spring.template.iam.core.config.properties.AuthProperties
import com.leijendary.spring.template.iam.core.exception.StatusException
import com.leijendary.spring.template.iam.core.util.SpringContext.Companion.getBean
import jakarta.servlet.http.HttpServletRequest
import org.springframework.context.i18n.LocaleContextHolder.getLocale
import org.springframework.context.i18n.LocaleContextHolder.getTimeZone
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.web.context.request.RequestContextHolder.getRequestAttributes
import org.springframework.web.context.request.ServletRequestAttributes
import java.net.URI
import java.time.OffsetDateTime
import java.time.ZoneId
import java.util.*

private val authProperties = getBean(AuthProperties::class)
private val sourceAuth = listOf("header", HEADER_USER_ID)

object RequestContext {
    val currentRequest: HttpServletRequest?
        get() {
            val attributes = getRequestAttributes() as? ServletRequestAttributes

            return attributes?.request
        }

    val userIdOrNull: UUID?
        get() = currentRequest?.getHeader(HEADER_USER_ID)?.let { UUID.fromString(it) }

    val userIdOrThrow: UUID
        get() = userIdOrNull ?: throw StatusException(sourceAuth, "access.session.notFound", UNAUTHORIZED)

    val userIdOrSystem: String
        get() = userIdOrNull?.toString() ?: authProperties.system.principal

    val uri: URI?
        get() {
            val request = currentRequest ?: return null
            val contextPath = request.contextPath
            var url = request.requestURI.replaceFirst(contextPath.toRegex(), "")

            request.queryString?.also { url += "?$it" }

            return URI.create(url)
        }

    val timeZone: TimeZone
        get() = getTimeZone()

    val zoneId: ZoneId
        get() = timeZone.toZoneId()

    val locale: Locale
        get() = getLocale()

    val language: String
        get() = locale.language

    val now: OffsetDateTime
        get() = OffsetDateTime.now(zoneId)
}
