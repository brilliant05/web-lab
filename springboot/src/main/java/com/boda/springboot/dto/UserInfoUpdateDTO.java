package com.boda.springboot.dto;

import lombok.Data;

@Data
public class UserInfoUpdateDTO {

    private String email;
    private String phone;
    private String avatarUrl;
    private String profile;

}
