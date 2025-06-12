package com.vissoft.vn.dbdocs.domain.entity;

import com.vissoft.vn.dbdocs.domain.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "version", schema = "dbdocs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Version extends BaseEntity {
    @Id
    @Column(name = "version_id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "project_id")
    private String projectId;

    @Column(name = "code_version")
    private Integer codeVersion;

    @Column(name = "change_log_id")
    private String changeLogId;
    
    @Column(name = "diff_change", columnDefinition = "TEXT")
    private String diffChange;
} 