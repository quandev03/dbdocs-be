package com.vissoft.vn.dbdocs.domain.entity;

import com.vissoft.vn.dbdocs.domain.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "project", schema = "dbdocs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Project extends BaseEntity {
    
    @Id
    @Column(name = "project_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String projectId;
    
    @Column(name = "project_code")
    private String projectCode;
    
    @Column(name = "project_name")
    private String projectName;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "password_share")
    private String passwordShare;
    
    @Column(name = "visibility", nullable = false)
    private Integer visibility = 2; // 1: public, 2: private, 3: protected
    
    @Column(name = "owner_id", nullable = false)
    private String ownerId;
} 