package com.vissoft.vn.dbdocs.infrastructure.adapter.port;

import com.vissoft.vn.dbdocs.domain.entity.Project;
import com.vissoft.vn.dbdocs.domain.repository.ProjectRepository;
import com.vissoft.vn.dbdocs.infrastructure.persistence.repository.ProjectRepositoryAdapter;
import com.vissoft.vn.dbdocs.infrastructure.mapper.ProjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProjectRepositoryPort implements ProjectRepositoryAdapter {

    private final ProjectRepository projectRepository;

    @Override
    public Project save(Project project) {
        return projectRepository.save(project);
    }

    @Override
    public Optional<Project> findById(String projectId) {
        return projectRepository.findById(projectId);
    }

    @Override
    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    @Override
    public void delete(Project project) {
//        projectRepository.delete(projectMapper.toEntity(project));
    }
}
