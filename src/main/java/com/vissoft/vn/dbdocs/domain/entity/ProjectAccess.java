package com.vissoft.vn.dbdocs.domain.entity;

import com.vissoft.vn.dbdocs.domain.entity.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "project_access", schema = "dbdocs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectAccess extends BaseEntity {
    
    @Id
    @Column(name = "project_access_id")
    private String projectAccessId;
    
    @Column(name = "project_id", nullable = false)
    private String projectId;
    
    @Column(name = "identifier")
    private String identifier; // social_id của người dùng
    
    @Column(nullable = false)
    private Integer permission = 1;
    
    @Column(name = "owner_id", nullable = false)
    private String ownerId;
} 