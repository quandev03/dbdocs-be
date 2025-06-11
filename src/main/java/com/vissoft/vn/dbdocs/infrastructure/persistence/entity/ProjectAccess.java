package com.vissoft.vn.dbdocs.infrastructure.persistence.entity;

import com.vissoft.vn.dbdocs.infrastructure.persistence.entity.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "project_access", schema = "dbdocs")
@Getter
@Setter
public class ProjectAccess extends BaseEntity {
    
    @Id
    @Column(name = "project_access_id")
    private String projectAccessId;
    
    @Column(name = "project_id", nullable = false)
    private String projectId;
    
    @Column(name = "user_id", nullable = false)
    private String userId;
    
    @Column(nullable = false)
    private Integer permission = 1;
    
    @Column(name = "owner_id", nullable = false)
    private String ownerId;
} 