package com.vissoft.vn.dbdocs.application.dto;

import lombok.Data;
import java.util.Date;

@Data
public class ProjectResponse {
    private String projectId;
    private String projectCode;
    private String description;
    private String passwordShare;
    private Integer visibility;
    private String ownerId;
    private Date createdDate;
    private String createdBy;
    private Date modifiedDate;
    private String modifiedBy;
} 