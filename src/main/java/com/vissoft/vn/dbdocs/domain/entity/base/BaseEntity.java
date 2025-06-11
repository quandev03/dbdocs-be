package com.vissoft.vn.dbdocs.domain.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity {
    
    @Column(name = "created_date")
    private Date createdDate;
    
    @Column(name = "created_by")
    private String createdBy;
    
    @Column(name = "modified_date")
    private Date modifiedDate;
    
    @Column(name = "modified_by")
    private String modifiedBy;
    
    @PrePersist
    protected void onCreate() {
        createdDate = new Date();
        createdBy = "system";
    }
    
    @PreUpdate
    protected void onUpdate() {
        modifiedDate = new Date();
        modifiedBy = "system";
    }
} 