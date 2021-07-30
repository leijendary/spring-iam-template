package com.leijendary.spring.iamtemplate.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "role")
@Data
public class RoleProperties {

    private Customer customer;

    @Data
    public static class Customer {

        private String name;
    }
}
