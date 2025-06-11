package com.vissoft.vn.dbdocs.infrastructure.persistence.repository;

import com.vissoft.vn.dbdocs.infrastructure.persistence.entity.ProjectAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectAccessRepository extends JpaRepository<ProjectAccess, String> {
} 