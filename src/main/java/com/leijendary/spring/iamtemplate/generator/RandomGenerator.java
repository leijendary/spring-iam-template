package com.leijendary.spring.iamtemplate.generator;

import java.util.Arrays;
import java.util.Random;

import static java.lang.Character.getNumericValue;

public class RandomGenerator {

    public static final Random RANDOM = new Random();
    public static final String NUMBERS = "0123456789";

    public static String otp() {
        final var digits = digits(6);
        final var builder = new StringBuilder();

        Arrays.stream(digits).forEach(builder::append);

        return builder.toString();
    }

    public static int[] digits(int length) {
        final var values = new int[length];

        for (int i = 0; i < length; i++) {
            final var index = RANDOM.nextInt(length);
            final var current = NUMBERS.charAt(index);

            values[i] = getNumericValue(current);
        }

        return values;
    }
}
