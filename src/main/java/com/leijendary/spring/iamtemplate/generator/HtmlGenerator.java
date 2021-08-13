package com.leijendary.spring.iamtemplate.generator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.util.Map;

import static com.leijendary.spring.iamtemplate.util.RequestContext.getLocale;

@Component
@RequiredArgsConstructor
public class HtmlGenerator {

    private final SpringTemplateEngine templateEngine;

    public String parse(final String templateName, final Map<String, Object> parameters) {
        final var context = new Context(getLocale(), parameters);

        return templateEngine.process(templateName, context);
    }
}
