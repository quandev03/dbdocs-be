package com.vissoft.vn.dbdocs.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DdlScriptResponse {
    private String projectId;
    private Integer fromVersion;
    private Integer toVersion;
    private Integer dialect;
    private String ddlScript;
} 