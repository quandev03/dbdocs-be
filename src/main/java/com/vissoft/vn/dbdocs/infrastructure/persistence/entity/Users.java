package com.vissoft.vn.dbdocs.infrastructure.persistence.entity;

import com.vissoft.vn.dbdocs.infrastructure.persistence.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users", schema = "dbdocs")
@Getter
@Setter
public class Users extends BaseEntity {
    
    @Id
    @Column(name = "user_id")
    private String userId;
    
    @Column(name = "social_id", unique = true)
    private String socialId;
    
    @Column(unique = true)
    private String email;
    
    @Column(name = "full_name")
    private String fullName;
    
    @Column(name = "avatar_url")
    private String avatarUrl;
    
    @Column(nullable = false)
    private Integer provider = 1;
} 