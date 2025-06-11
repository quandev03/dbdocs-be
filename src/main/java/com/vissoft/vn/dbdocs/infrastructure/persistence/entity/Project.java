package com.vissoft.vn.dbdocs.infrastructure.persistence.entity;

import com.vissoft.vn.dbdocs.infrastructure.persistence.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "project", schema = "dbdocs")
@Getter
@Setter
public class Project extends BaseEntity {
    
    @Id
    @Column(name = "project_id")
    private String projectId;
    
    @Column(name = "project_code")
    private String projectCode;
    
    private String description;
    
    @Column(name = "password_share")
    private String passwordShare;
    
    @Column(nullable = false)
    private Integer visibility = 2;
    
    @Column(name = "owner_id", nullable = false)
    private String ownerId;
} 