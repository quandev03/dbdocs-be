package com.vissoft.vn.dbdocs.domain.service.impl;

import com.vissoft.vn.dbdocs.application.dto.ProjectCreateRequest;
import com.vissoft.vn.dbdocs.application.dto.ProjectDTO;
import com.vissoft.vn.dbdocs.application.dto.ProjectResponse;
import com.vissoft.vn.dbdocs.application.dto.ProjectUpdateRequest;
import com.vissoft.vn.dbdocs.domain.entity.Project;
import com.vissoft.vn.dbdocs.domain.entity.ProjectAccess;
import com.vissoft.vn.dbdocs.domain.entity.Users;
import com.vissoft.vn.dbdocs.domain.repository.ProjectAccessRepository;
import com.vissoft.vn.dbdocs.domain.repository.ProjectRepository;
import com.vissoft.vn.dbdocs.domain.repository.UserRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectManagerServiceImpl implements ProjectManagerService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final ProjectAccessService projectAccessService;
    private final ProjectAccessRepository projectAccessRepository;
    private final UserRepository userRepository;

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
            
            // Get current user information
            Users currentUser = userRepository.findById(currentUserId).orElse(null);
            ProjectDTO projectDTO = projectMapper.toDTO(savedProject);
            
            // Set owner information
            if (currentUser != null) {
                projectDTO.setOwnerEmail(currentUser.getEmail());
                projectDTO.setOwnerAvatarUrl(currentUser.getAvatarUrl());
                log.debug("Added owner information - email: {}, avatarUrl: {}", 
                        currentUser.getEmail(), currentUser.getAvatarUrl());
            }
            
            return projectDTO;
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
            
            // Get owner information
            Users owner = userRepository.findById(updatedProject.getOwnerId()).orElse(null);
            ProjectDTO projectDTO = projectMapper.toDTO(updatedProject);
            
            // Set owner information
            if (owner != null) {
                projectDTO.setOwnerEmail(owner.getEmail());
                projectDTO.setOwnerAvatarUrl(owner.getAvatarUrl());
                log.debug("Added owner information - email: {}, avatarUrl: {}", 
                        owner.getEmail(), owner.getAvatarUrl());
            }
            
            return projectDTO;
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
            
            // Get owner information
            Users owner = userRepository.findById(project.getOwnerId()).orElse(null);
            ProjectDTO projectDTO = projectMapper.toDTO(project);
            
            if (owner != null) {
                projectDTO.setOwnerEmail(owner.getEmail());
                projectDTO.setOwnerAvatarUrl(owner.getAvatarUrl());
                log.debug("Added owner information - email: {}, avatarUrl: {}", 
                        owner.getEmail(), owner.getAvatarUrl());
            } else {
                log.warn("Owner not found for project: {}", project.getProjectId());
            }
            
            return projectDTO;
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
            
            // Get current user information for setting owner details
            Users currentUser = userRepository.findById(currentUserId).orElse(null);
            
            return projects.stream()
                    .map(project -> {
                        ProjectDTO dto = projectMapper.toDTO(project);
                        
                        // Since these are the user's own projects, set owner info from current user
                        if (currentUser != null) {
                            dto.setOwnerEmail(currentUser.getEmail());
                            dto.setOwnerAvatarUrl(currentUser.getAvatarUrl());
                        }
                        
                        return dto;
                    })
                    .toList();
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
    
    @Override
    public List<ProjectResponse> getSharedProjects() {
        String currentUserId = SecurityUtils.getCurrentUserId();
        log.info("Fetching shared projects for user: {}", currentUserId);
        
        try {
            // Tìm tất cả các project access của user hiện tại
            List<ProjectAccess> projectAccesses = projectAccessRepository.findByIdentifier(currentUserId);
            log.info("Found {} shared projects for user", projectAccesses.size());
            
            if (projectAccesses.isEmpty()) {
                return new ArrayList<>();
            }
            
            // Lấy danh sách projectId từ projectAccesses
            List<String> projectIds = projectAccesses.stream()
                    .map(ProjectAccess::getProjectId)
                    .toList();
            
            // Tìm tất cả các project theo danh sách projectId
            List<Project> projects = projectRepository.findAllById(projectIds);
            
            // Loại bỏ các project mà user là owner (vì đã được trả về trong getAllProjects)
            List<Project> sharedProjects = projects.stream()
                    .filter(project -> !project.getOwnerId().equals(currentUserId))
                    .toList();
            
            log.info("Processing {} shared projects with owner information", sharedProjects.size());
            
            List<ProjectResponse> result = new ArrayList<>();
            
            for (Project project : sharedProjects) {
                // Tìm thông tin owner của project
                Users owner = userRepository.findById(project.getOwnerId()).orElse(null);
                
                if (owner != null) {
                    log.debug("Found owner: {} for project: {}", owner.getEmail(), project.getProjectId());
                } else {
                    log.warn("Owner not found for project: {}", project.getProjectId());
                }
                
                // Tạo response với thông tin owner
                ProjectResponse response = projectMapper.toResponseWithOwner(project, owner);
                result.add(response);
            }
            
            log.info("Returning {} shared projects with owner information", result.size());
            
            return result;
        } catch (Exception e) {
            log.error("Error fetching shared projects for user: {}", currentUserId, e);
            throw BaseException.of(ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
