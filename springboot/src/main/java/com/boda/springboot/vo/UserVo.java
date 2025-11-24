package com.boda.springboot.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVo {
    private Long userId;
    private String username;
    private String realName;
    private String role;
    private String avatarUrl;
}
