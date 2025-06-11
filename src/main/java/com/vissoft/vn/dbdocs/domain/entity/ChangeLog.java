package com.vissoft.vn.dbdocs.domain.entity;

import com.vissoft.vn.dbdocs.domain.entity.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "change_log", schema = "dbdocs")
@Getter
@Setter
public class ChangeLog extends BaseEntity {
    
    @Id
    @Column(name = "change_log_id")
    private String changeLogId;
    
    @Column(name = "project_id", nullable = false)
    private String projectId;
    
    @Column(nullable = false)
    private String content;
    
    @Column(name = "code_change_log")
    private String codeChangeLog;
} 