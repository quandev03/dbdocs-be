package com.vissoft.vn.dbdocs.application.dto;

import com.vissoft.vn.dbdocs.infrastructure.constant.Constants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SingleVersionDdlRequest {
    @NotBlank(message = "Project ID is required")
    private String projectId;
    
    @NotNull(message = "Version number is required")
    private Integer versionNumber;
    
    private Integer dialect = Constants.SQL.Dialect.MYSQL; // Default dialect: MySQL
} 