package com.vissoft.vn.dbdocs.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCreateRequest {
    private String projectCode;
    
    private String description;
    
    private String passwordShare;
    
    private Integer visibility = 2; // Default to private
} 