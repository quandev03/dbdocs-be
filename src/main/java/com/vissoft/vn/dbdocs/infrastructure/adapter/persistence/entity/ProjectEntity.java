package com.vissoft.vn.dbdocs.infrastructure.adapter.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "projects")
public class ProjectEntity {
    @Id
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