package com.vissoft.vn.dbdocs.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectPermissionRequest {
    private String projectId;
    private String identifier; // social_id của người dùng
    private Integer permission; // 1: view, 2: edit
} 