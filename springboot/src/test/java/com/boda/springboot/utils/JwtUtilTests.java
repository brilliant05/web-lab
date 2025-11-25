package com.boda.springboot.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * JwtUtil 单元测试
 */
@SpringBootTest
public class JwtUtilTests {

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void testGenerateAndParseToken() {
        String token = jwtUtil.generateToken(1001L, "alice", "ADMIN");
        System.out.println("生成的 Token: " + token);
        Assertions.assertNotNull(token, "生成的 token 不应为 null");

        Assertions.assertTrue(jwtUtil.validateToken(token), "token 应该有效");
        Assertions.assertEquals("alice", jwtUtil.getUsernameFromToken(token));
        Assertions.assertEquals(1001L, jwtUtil.getUserIdFromToken(token));
        Assertions.assertEquals("ADMIN", jwtUtil.getRoleFromToken(token));
    }

    @Test
    void testInvalidToken() {
        String invalid = "abc.def.ghi"; // 非法结构
        Assertions.assertFalse(jwtUtil.validateToken(invalid));
    }
}

