package com.vissoft.vn.dbdocs.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VersionDTO {
    private String id;
    private String projectId;
    private Integer codeVersion;
    private String changeLogId;
    private String diffChange;
    private ChangeLogDTO changeLog;
    private String content;
    private Instant createdDate;
    private String createdBy;
} 