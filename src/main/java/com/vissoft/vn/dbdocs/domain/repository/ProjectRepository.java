package com.vissoft.vn.dbdocs.domain.repository;

import com.vissoft.vn.dbdocs.domain.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {
} 