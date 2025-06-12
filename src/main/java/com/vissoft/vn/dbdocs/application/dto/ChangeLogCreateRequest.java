package com.vissoft.vn.dbdocs.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeLogCreateRequest {
    @NotBlank(message = "Project ID is required")
    private String projectId;
    
    @NotBlank(message = "Content is required")
    private String content;
} 