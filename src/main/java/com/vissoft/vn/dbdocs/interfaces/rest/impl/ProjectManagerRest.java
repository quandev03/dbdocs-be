package com.vissoft.vn.dbdocs.interfaces.rest.impl;

import com.vissoft.vn.dbdocs.application.dto.ProjectCreateRequest;
import com.vissoft.vn.dbdocs.application.dto.ProjectDTO;
import com.vissoft.vn.dbdocs.application.dto.ProjectResponse;
import com.vissoft.vn.dbdocs.application.dto.ProjectUpdateRequest;
import com.vissoft.vn.dbdocs.domain.service.ProjectManagerService;
import com.vissoft.vn.dbdocs.infrastructure.security.SecurityUtils;
import com.vissoft.vn.dbdocs.interfaces.rest.ProjectManagerOperator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ProjectManagerRest implements ProjectManagerOperator {

    private final ProjectManagerService projectManagerService;

    @Override
    public ResponseEntity<ProjectDTO> createProject(ProjectCreateRequest request) {
        return ResponseEntity.ok(projectManagerService.createProject(request));
    }

    @Override
    public ResponseEntity<ProjectDTO> updateProject(String projectId, ProjectUpdateRequest request) {
        return ResponseEntity.ok(projectManagerService.updateProject(projectId, request));
    }

    @Override
    public ResponseEntity<ProjectDTO> getProjectById(String projectId) {
        return ResponseEntity.ok(projectManagerService.getProjectById(projectId));
    }

    @Override
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        log.info("REST request to get all projects for user: {}", SecurityUtils.getCurrentUserId());
        return ResponseEntity.ok(projectManagerService.getAllProjects());
    }

    @Override
    public ResponseEntity<List<ProjectResponse>> getSharedProjects() {
        log.info("REST request to get shared projects for user: {}", SecurityUtils.getCurrentUserId());
        return ResponseEntity.ok(projectManagerService.getSharedProjects());
    }

    @Override
    public ResponseEntity<Void> deleteProject(String projectId) {
        projectManagerService.deleteProject(projectId);
        return ResponseEntity.ok().build();
    }
}