package com.leijendary.spring.template.iam.generator

import com.leijendary.spring.template.iam.core.util.RequestContext.locale
import org.springframework.stereotype.Component
import org.thymeleaf.context.Context
import org.thymeleaf.spring6.SpringTemplateEngine

@Component
class HtmlGenerator(private val templateEngine: SpringTemplateEngine) {
    fun parse(templateName: String, parameters: Map<String, Any?>): String {
        val context = Context(locale, parameters)

        return templateEngine.process(templateName, context)
    }
}
