package com.vissoft.vn.dbdocs.application.dto;

import com.vissoft.vn.dbdocs.infrastructure.constant.Constants;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeLogDdlRequest {
    @NotBlank(message = "Project ID is required")
    private String projectId;
    
    @NotBlank(message = "Changelog code is required")
    private String changeLogCode;
    
    private Integer dialect = Constants.SQL.Dialect.MYSQL; // Default dialect: MySQL
} 