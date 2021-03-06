package com.leijendary.spring.iamtemplate.config;

import com.leijendary.spring.iamtemplate.config.properties.InfoProperties;
import com.leijendary.spring.iamtemplate.data.AppPageable;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static springfox.documentation.spi.DocumentationType.OAS_30;

@Configuration
@EnableSwagger2
@RequiredArgsConstructor
public class SwaggerConfiguration {

    private final InfoProperties infoProperties;

    @Bean
    public Docket api() {
        return new Docket(OAS_30)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo())
                .directModelSubstitute(Pageable.class, AppPageable.class)
                .genericModelSubstitutes(CompletableFuture.class, ResponseEntity.class)
                .securityContexts(List.of(securityContext()))
                .securitySchemes(List.of(apiKey()));
    }

    private ApiInfo apiInfo() {
        final var app = infoProperties.getApp();
        final var api = infoProperties.getApi();

        return new ApiInfo(
                app.getName(),
                app.getDescription(),
                app.getVersion(),
                api.getTermsOfServiceUrl(),
                api.getContact(),
                api.getLicense(),
                api.getLicenseUrl(),
                api.getVendorExtensions());
    }

    public SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .build();
    }

    private List<SecurityReference> defaultAuth() {
        final var authorizationScope = new AuthorizationScope("global", "accessEverything");
        final var authorizationScopes = new AuthorizationScope[] { authorizationScope };

        return List.of(new SecurityReference("Authorization", authorizationScopes));
    }

    private ApiKey apiKey() {
        return new ApiKey("Authorization", "Authorization", "header");
    }
}
