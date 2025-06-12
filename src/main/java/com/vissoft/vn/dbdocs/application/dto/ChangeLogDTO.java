package com.vissoft.vn.dbdocs.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangeLogDTO {
    private String changeLogId;
    private String projectId;
    private String content;
    private String codeChangeLog;
    private Date createdDate;
    private String createdBy;
    private Date modifiedDate;
    private String modifiedBy;
} 