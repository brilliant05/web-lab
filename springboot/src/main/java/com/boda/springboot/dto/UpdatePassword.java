package com.boda.springboot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePassword {
    /**
     * 用户名（可选，优先从 Token 获取）
     */
    private String username;

    /**
     * 原密码
     */
    private String oldPassword;

    /**
     * 新密码
     */
    private String newPassword;

    /**
     * 确认新密码（可选）
     */
    private String confirmPassword;
}
