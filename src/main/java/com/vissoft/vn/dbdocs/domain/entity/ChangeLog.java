package com.vissoft.vn.dbdocs.domain.entity;

import com.vissoft.vn.dbdocs.domain.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
@Table(name = "change_log", schema = "dbdocs")
@NoArgsConstructor
@AllArgsConstructor
public class ChangeLog extends BaseEntity {
    
    @Id
    @Column(name = "change_log_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(name = "project_id", nullable = false)
    private String projectId;
    
    @Column(nullable = false)
    private String content;
    
    @Column(name = "code_change_log")
    private String codeChangeLog;
} 