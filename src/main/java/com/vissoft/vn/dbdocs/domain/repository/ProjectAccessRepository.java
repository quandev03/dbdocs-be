package com.vissoft.vn.dbdocs.domain.repository;

import com.vissoft.vn.dbdocs.domain.entity.ProjectAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectAccessRepository extends JpaRepository<ProjectAccess, String> {
} 