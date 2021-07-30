package com.leijendary.spring.iamtemplate.config;

import com.leijendary.spring.iamtemplate.config.properties.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        AspectProperties.class,
        AuthProperties.class,
        CorsProperties.class,
        InfoProperties.class,
        RoleProperties.class,
        VerificationProperties.class })
public class PropertiesConfiguration {
}
