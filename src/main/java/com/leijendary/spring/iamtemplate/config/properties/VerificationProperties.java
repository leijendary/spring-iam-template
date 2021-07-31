package com.leijendary.spring.iamtemplate.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "verification")
@Data
public class VerificationProperties {

    // In minutes
    private int expiry;
    private Register register;

    @Data
    public static class Register {

        private String url;
    }
}
