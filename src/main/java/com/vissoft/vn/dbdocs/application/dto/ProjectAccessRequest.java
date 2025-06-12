package com.vissoft.vn.dbdocs.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectAccessRequest {
    private String projectId;
    private String emailOrUsername;
    private Integer permission = 1; // Default is view permission
} 