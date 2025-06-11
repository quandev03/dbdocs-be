package com.vissoft.vn.dbdocs.interfaces.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenResponseDto {
    private String accessToken;
    private String tokenType;
    private Long expiresIn;
} 