package com.vissoft.vn.dbdocs.infrastructure.adapter;
import com.vissoft.vn.dbdocs.domain.entity.Project;

import java.util.List;
import java.util.Optional;

public interface ProjectRepositoryAdapter {
    Project save(Project project);
    Optional<Project> findById(String projectId);
    List<Project> findAll();
    void delete(Project project);
}
