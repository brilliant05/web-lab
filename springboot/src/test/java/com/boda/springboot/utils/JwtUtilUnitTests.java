package com.boda.springboot.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

/**
 * 不依赖 Spring 上下文的 JwtUtil 单元测试
 */
public class JwtUtilUnitTests {

    private void injectField(Object target, String name, Object value) {
        try {
            Field f = target.getClass().getDeclaredField(name);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGenerateParseAndValidate() {
        JwtUtil jwtUtil = new JwtUtil();
        // secret 至少 32 字节
        injectField(jwtUtil, "secret", "0123456789abcdef0123456789abcdef");
        injectField(jwtUtil, "expiration", 3600_000L); // 1 小时

        String token = jwtUtil.generateToken(1001L, "alice", "ADMIN");
        System.out.println("生成的 Token: " + token);
        Assertions.assertNotNull(token, "生成的 token 不应为 null");

        Assertions.assertTrue(jwtUtil.validateToken(token), "token 应该有效");
        Assertions.assertEquals("alice", jwtUtil.getUsernameFromToken(token));
        Assertions.assertEquals(1001L, jwtUtil.getUserIdFromToken(token));
        Assertions.assertEquals("ADMIN", jwtUtil.getRoleFromToken(token));
        Assertions.assertFalse(jwtUtil.isTokenExpired(token), "新生成的 token 不应过期");
    }

    @Test
    void testExpiredToken() throws InterruptedException {
        JwtUtil jwtUtil = new JwtUtil();
        injectField(jwtUtil, "secret", "0123456789abcdef0123456789abcdef");
        injectField(jwtUtil, "expiration", 1L); // 极短过期

        String token = jwtUtil.generateToken(2002L, "bob", "USER");
        // 等待确保过期
        Thread.sleep(10);

        Assertions.assertTrue(jwtUtil.isTokenExpired(token), "token 应该已过期");
        Assertions.assertFalse(jwtUtil.validateToken(token), "过期的 token 应被判定为无效");
    }

    @Test
    void testTamperedTokenIsInvalid() {
        JwtUtil jwtUtil = new JwtUtil();
        injectField(jwtUtil, "secret", "0123456789abcdef0123456789abcdef");
        injectField(jwtUtil, "expiration", 3600_000L);

        String token = jwtUtil.generateToken(3003L, "carol", "GUEST");
        // 简单篡改：改变最后一个字符
        String tampered = token.substring(0, token.length() - 1) +
                (token.charAt(token.length() - 1) == 'a' ? 'b' : 'a');

        Assertions.assertFalse(jwtUtil.validateToken(tampered), "篡改后的 token 应无效");
    }
}

