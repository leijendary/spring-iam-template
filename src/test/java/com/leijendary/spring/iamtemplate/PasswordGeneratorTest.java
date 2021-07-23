package com.leijendary.spring.iamtemplate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordGeneratorTest extends ApplicationTests {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void generatePassword() {
        final var clearText = "1234";
        final var bcrypt = passwordEncoder.encode(clearText);

        System.out.println(bcrypt);
    }
}
