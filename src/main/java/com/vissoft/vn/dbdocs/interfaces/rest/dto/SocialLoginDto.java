package com.vissoft.vn.dbdocs.interfaces.rest.dto;

import lombok.Data;

@Data
public class SocialLoginDto {
    private String socialId;
    private String email;
    private String fullName;
    private String avatarUrl;
    private Integer provider; // 1: google, 2: github
} 