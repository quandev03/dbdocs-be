package com.vissoft.vn.dbdocs.infrastructure.persistence.repository;
import com.vissoft.vn.dbdocs.domain.entity.Project;

import java.util.List;
import java.util.Optional;

public interface ProjectRepositoryAdapter {
    Project save(Project project);
    Optional<Project> findById(String projectId);
    List<Project> findAll();
    void delete(Project project);
}
