package com.vissoft.vn.dbdocs.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectAccessDTO {
    private String id;
    private String projectId;
    private String identifier;
    private Integer permission;
    private String userEmail;
    private String userName;
    private String avatarUrl;
    private Instant createdDate;
    private String createdBy;
} 