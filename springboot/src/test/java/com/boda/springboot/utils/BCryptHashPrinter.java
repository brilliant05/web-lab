package com.boda.springboot.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class BCryptHashPrinter {
    public static void main(String[] args) {
        String raw = args != null && args.length > 0 ? args[0] : "admin123";
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode(raw);
        System.out.println("Raw: " + raw);
        System.out.println("BCrypt: " + hash);
    }
}

