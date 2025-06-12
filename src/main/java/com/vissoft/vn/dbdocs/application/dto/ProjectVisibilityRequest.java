package com.vissoft.vn.dbdocs.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectVisibilityRequest {
    private String projectId;
    private Integer visibility; // 1: public, 2: private, 3: protected
    private String password; // Chỉ cần khi visibility = 3 (protected)
} 