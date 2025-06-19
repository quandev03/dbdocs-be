package com.vissoft.vn.dbdocs.interfaces.rest.impl;

import com.vissoft.vn.dbdocs.application.dto.ProjectCreateRequest;
import com.vissoft.vn.dbdocs.application.dto.ProjectDTO;
import com.vissoft.vn.dbdocs.application.dto.ProjectResponse;
import com.vissoft.vn.dbdocs.application.dto.ProjectUpdateRequest;
import com.vissoft.vn.dbdocs.domain.service.ProjectManagerService;
import com.vissoft.vn.dbdocs.infrastructure.security.SecurityUtils;
import com.vissoft.vn.dbdocs.interfaces.rest.ProjectManagerOperator;
import com.vissoft.vn.dbdocs.interfaces.rest.dto.InputPasswordShare;
import com.vissoft.vn.dbdocs.interfaces.rest.dto.ShareDbDocsDto;
import com.vissoft.vn.dbdocs.interfaces.rest.dto.ShareDbDocsRequest;
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

    /**
     * Creates a new project.
     *
     * @param request The request containing project creation details.
     * @return ResponseEntity containing the created ProjectDTO.
     */
    @Override
    public ResponseEntity<ProjectDTO> createProject(ProjectCreateRequest request) {
        return ResponseEntity.ok(projectManagerService.createProject(request));
    }

    /**
     * Updates an existing project.
     *
     * @param projectId The ID of the project to update.
     * @param request   The request containing updated project details.
     * @return ResponseEntity containing the updated ProjectDTO.
     */
    @Override
    public ResponseEntity<ProjectDTO> updateProject(String projectId, ProjectUpdateRequest request) {
        return ResponseEntity.ok(projectManagerService.updateProject(projectId, request));
    }

    /**
     * Retrieves a project by its ID.
     *
     * @param projectId The ID of the project to retrieve.
     * @return ResponseEntity containing the ProjectDTO if found, or 404 Not Found if not found.
     */
    @Override
    public ResponseEntity<ProjectDTO> getProjectById(String projectId) {
        return ResponseEntity.ok(projectManagerService.getProjectById(projectId));
    }

    /**
     * Retrieves a shared project by its ID, using an optional password for access.
     *
     * @param projectId The ID of the shared project to retrieve.
     * @param inputPasswordShare Optional password for accessing the shared project.
     * @return ResponseEntity containing the ProjectDTO if found, or 404 Not Found if not found.
     */
    @Override
    public ResponseEntity<ProjectDTO> getProjectSharedById(String projectId,
                                                            InputPasswordShare inputPasswordShare){
        log.info("REST request to get shared project by ID: {}", projectId);
        ProjectDTO project = projectManagerService.getProjectById(projectId, inputPasswordShare);
        if (project == null) {
            log.warn("Project with ID {} not found", projectId);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(project);
    }

    /**
     * Retrieves all projects for the current user.
     *
     * @return ResponseEntity containing a list of ProjectDTOs.
     */
    @Override
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        log.info("REST request to get all projects for user: {}", SecurityUtils.getCurrentUserId());
        return ResponseEntity.ok(projectManagerService.getAllProjects());
    }

    /**
     * Retrieves all shared projects for the current user.
     *
     * @return ResponseEntity containing a list of ProjectResponse objects.
     */
    @Override
    public ResponseEntity<List<ProjectResponse>> getSharedProjects() {
        log.info("REST request to get shared projects for user: {}", SecurityUtils.getCurrentUserId());
        return ResponseEntity.ok(projectManagerService.getSharedProjects());
    }

    /**
     * Deletes a project by its ID.
     *
     * @param projectId The ID of the project to delete.
     * @return ResponseEntity with status 200 OK if successful, or 404 Not Found if project not found.
     */
    @Override
    public ResponseEntity<Void> deleteProject(String projectId) {
        projectManagerService.deleteProject(projectId);
        return ResponseEntity.ok().build();
    }

    /**
     * Shares a project with specified users or groups.
     *
     * @param projectId The ID of the project to share.
     * @param shareDbDocsRequest The request containing sharing details.
     * @return ResponseEntity containing ShareDbDocsDto with sharing details.
     */
    @Override
    public ResponseEntity<ShareDbDocsDto> shareProject(String projectId, ShareDbDocsRequest shareDbDocsRequest){
        log.info("REST request to share project with ID: {}", projectId);
        return ResponseEntity.ok(projectManagerService.shareProject(projectId, shareDbDocsRequest));
    }
}