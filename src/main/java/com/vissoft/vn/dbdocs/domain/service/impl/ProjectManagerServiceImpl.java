package com.vissoft.vn.dbdocs.domain.service.impl;

import com.vissoft.vn.dbdocs.application.dto.ProjectCreateRequest;
import com.vissoft.vn.dbdocs.application.dto.ProjectDTO;
import com.vissoft.vn.dbdocs.application.dto.ProjectUpdateRequest;
import com.vissoft.vn.dbdocs.domain.entity.Project;
import com.vissoft.vn.dbdocs.domain.repository.ProjectRepository;
import com.vissoft.vn.dbdocs.domain.service.ProjectAccessService;
import com.vissoft.vn.dbdocs.domain.service.ProjectManagerService;
import com.vissoft.vn.dbdocs.infrastructure.exception.BaseException;
import com.vissoft.vn.dbdocs.infrastructure.exception.ErrorCode;
import com.vissoft.vn.dbdocs.infrastructure.mapper.ProjectMapper;
import com.vissoft.vn.dbdocs.infrastructure.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectManagerServiceImpl implements ProjectManagerService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final ProjectAccessService projectAccessService;

    @Override
    @Transactional
    public ProjectDTO createProject(ProjectCreateRequest request) {
        String currentUserId = SecurityUtils.getCurrentUserId();
        log.info("Creating new project by user: {}", currentUserId);
        
        try {
            Project project = projectMapper.projectCreateRequestToEntity(request);
            project.setOwnerId(currentUserId);
            
            // Lưu project trước
            Project savedProject = projectRepository.save(project);
            log.info("Project created successfully with ID: {}", savedProject.getProjectId());
            log.info("Owner added to project access - projectId: {}, ownerId: {}", 
                    savedProject.getProjectId(), currentUserId);
            
            return projectMapper.toDTO(savedProject);
        } catch (Exception e) {
            log.error("Error creating project", e);
            throw e;
        }
    }

    @Override
    @Transactional
    public ProjectDTO updateProject(String projectId, ProjectUpdateRequest request) {
        log.info("Updating project: {}", projectId);
        
        try {
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> {
                        log.error("Project not found with ID: {}", projectId);
                        return BaseException.of(ErrorCode.PROJECT_NOT_FOUND);
                    });
                    
            if (!project.getOwnerId().equals(SecurityUtils.getCurrentUserId())) {
                log.error("Permission denied - user: {} is not owner of project: {}", 
                        SecurityUtils.getCurrentUserId(), projectId);
                throw BaseException.of(ErrorCode.NOT_PROJECT_OWNER, HttpStatus.FORBIDDEN);
            }

            log.info("Updating project details - old code: {}, new code: {}", 
                    project.getProjectCode(), request.getProjectCode());
            
            project.setProjectCode(request.getProjectCode());
            project.setDescription(request.getDescription());

            Project updatedProject = projectRepository.save(project);
            log.info("Project updated successfully - ID: {}", updatedProject.getProjectId());
            
            return projectMapper.toDTO(updatedProject);
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error updating project: {}", projectId, e);
            throw BaseException.of(ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ProjectDTO getProjectById(String projectId) {
        log.info("Fetching project by ID: {}", projectId);
        
        try {
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> {
                        log.error("Project not found with ID: {}", projectId);
                        return BaseException.of(ErrorCode.PROJECT_NOT_FOUND);
                    });
            
            log.info("Project found: {}", project.getProjectCode());
            return projectMapper.toDTO(project);
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching project: {}", projectId, e);
            throw BaseException.of(ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<ProjectDTO> getAllProjects() {
        String currentUserId = SecurityUtils.getCurrentUserId();
        log.info("Fetching all projects for user: {}", currentUserId);
        
        try {
            List<Project> projects = projectRepository.findByOwnerId(currentUserId);
            log.info("Found {} projects for user", projects.size());
            
            return projects.stream()
                    .map(projectMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching projects for user: {}", currentUserId, e);
            throw BaseException.of(ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public void deleteProject(String projectId) {
        String currentUserId = SecurityUtils.getCurrentUserId();
        log.info("Deleting project: {} by user: {}", projectId, currentUserId);
        
        try {
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> {
                        log.error("Project not found with ID: {}", projectId);
                        return BaseException.of(ErrorCode.PROJECT_NOT_FOUND);
                    });
                    
            if (!project.getOwnerId().equals(currentUserId)) {
                log.error("Permission denied - user: {} is not owner of project: {}", 
                        currentUserId, projectId);
                throw BaseException.of(ErrorCode.NOT_PROJECT_OWNER, HttpStatus.FORBIDDEN);
            }

            projectRepository.delete(project);
            log.info("Project deleted successfully - ID: {}", projectId);
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error deleting project: {}", projectId, e);
            throw BaseException.of(ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
