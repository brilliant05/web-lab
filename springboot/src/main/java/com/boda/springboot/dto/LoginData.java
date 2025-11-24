package com.boda.springboot.dto;

import com.boda.springboot.vo.UserVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginData {
    private String token;
    private UserVo userInfo;

}
