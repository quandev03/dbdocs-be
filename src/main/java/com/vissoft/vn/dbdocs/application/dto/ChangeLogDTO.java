package com.vissoft.vn.dbdocs.application.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    
    // Thông tin avatar của người tạo
    private String creatorName;
    private String creatorAvatarUrl;
    
    // Thông tin avatar của người chỉnh sửa
    private String modifierName;
    private String modifierAvatarUrl;
} 