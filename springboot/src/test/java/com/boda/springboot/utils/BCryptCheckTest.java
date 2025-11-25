package com.boda.springboot.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class BCryptCheckTest {

    @Test
    void hashShouldMatchAdmin123() {
        String raw = "admin123";
        String hash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi";
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        Assertions.assertTrue(encoder.matches(raw, hash), "BCrypt hash does not match admin123");
    }
}

