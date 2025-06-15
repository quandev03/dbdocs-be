package com.vissoft.vn.dbdocs.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String userId;
    private String email;
    private String fullName;
    private String avatarUrl;
    private Integer provider;
} 