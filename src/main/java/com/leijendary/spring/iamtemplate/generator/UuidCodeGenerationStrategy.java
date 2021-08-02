package com.leijendary.spring.iamtemplate.generator;

import static java.util.UUID.randomUUID;

public class UuidCodeGenerationStrategy implements CodeGenerationStrategy {

    @Override
    public String generate() {
        return randomUUID().toString();
    }
}
