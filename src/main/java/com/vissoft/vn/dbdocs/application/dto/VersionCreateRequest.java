package com.vissoft.vn.dbdocs.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VersionCreateRequest {
    @NotBlank(message = "Project ID is required")
    private String projectId;
    
    private String changeLogId;
} 