package com.leijendary.spring.iamtemplate.config;

import com.leijendary.spring.iamtemplate.config.properties.AspectProperties;
import com.leijendary.spring.iamtemplate.config.properties.AuthProperties;
import com.leijendary.spring.iamtemplate.config.properties.CorsProperties;
import com.leijendary.spring.iamtemplate.config.properties.InfoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        AspectProperties.class,
        AuthProperties.class,
        CorsProperties.class,
        InfoProperties.class })
public class PropertiesConfiguration {
}
