package com.leijendary.spring.iamtemplate.generator;

import java.util.Arrays;

import static com.leijendary.spring.iamtemplate.generator.RandomGenerator.digits;

public class OtpCodeGenerationStrategy implements CodeGenerationStrategy {

    @Override
    public String generate() {
        final var digits = digits(6);
        final var builder = new StringBuilder();

        Arrays.stream(digits).forEach(builder::append);

        return builder.toString();
    }
}
