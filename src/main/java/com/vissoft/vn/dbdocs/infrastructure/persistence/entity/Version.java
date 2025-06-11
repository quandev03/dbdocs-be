package com.vissoft.vn.dbdocs.infrastructure.persistence.entity;

import com.vissoft.vn.dbdocs.infrastructure.persistence.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "version", schema = "dbdocs")
@Getter
@Setter
public class Version extends BaseEntity {
    
    @Id
    @Column(name = "version_id")
    private String versionId;
    
    @Column(name = "code_version")
    private Integer codeVersion;
    
    @Column(name = "change_log_id")
    private String changeLogId;
    
    @Column(name = "diff_change", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String diffChange;
} 