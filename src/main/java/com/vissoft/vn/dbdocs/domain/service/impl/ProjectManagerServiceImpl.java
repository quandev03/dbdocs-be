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
import com.vissoft.vn.dbdocs.infrastructure.constant.Constants;
import com.vissoft.vn.dbdocs.infrastructure.exception.BaseException;
import com.vissoft.vn.dbdocs.infrastructure.exception.ErrorCode;
import com.vissoft.vn.dbdocs.infrastructure.mapper.ProjectMapper;
import com.vissoft.vn.dbdocs.infrastructure.security.SecurityUtils;
import com.vissoft.vn.dbdocs.infrastructure.util.DataUtils;
import com.vissoft.vn.dbdocs.interfaces.rest.dto.InputPasswordShare;
import com.vissoft.vn.dbdocs.interfaces.rest.dto.ShareDbDocsDto;
import com.vissoft.vn.dbdocs.interfaces.rest.dto.ShareDbDocsRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectManagerServiceImpl implements ProjectManagerService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final ProjectAccessService projectAccessService;
    private final ProjectAccessRepository projectAccessRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Value("${domain.frontend.url}")
    private String frontendUrl;

    @Override
    @Transactional
    public ProjectDTO createProject(ProjectCreateRequest request) {
        String currentUserId = SecurityUtils.getCurrentUserId();
        log.info("Creating new project by user: {}", currentUserId);
        
        try {
            Project project = projectMapper.projectCreateRequestToEntity(request);
            project.setOwnerId(currentUserId);
            
            // Save the project to the repository
            Project savedProject = projectRepository.save(project);
            log.info("Project created successfully with ID: {}", savedProject.getProjectId());
            log.info("Owner added to project access - projectId: {}, ownerId: {}", 
                    savedProject.getProjectId(), currentUserId);
            
            // Get current user information
            Users currentUser = userRepository.findById(currentUserId).orElse(null);
            ProjectDTO projectDTO = projectMapper.toDTO(savedProject);
            
            // Set owner information
            if (DataUtils.notNull(currentUser)) {
                projectDTO.setOwnerEmail(currentUser.getEmail());
                projectDTO.setOwnerAvatarUrl(currentUser.getAvatarUrl());
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
            Project project = getAndCheckProject(projectId);

            if (!Objects.equals(project.getOwnerId(), SecurityUtils.getCurrentUserId())) {
                log.error(ErrorCode.NOT_PROJECT_OWNER.name());
                throw BaseException.of(ErrorCode.NOT_PROJECT_OWNER, HttpStatus.FORBIDDEN);
            }
            log.info("Updating project details - old code: {}, new code: {}", 
                    project.getProjectCode(), request.getProjectCode());
            project.setDescription(request.getDescription());
            Project updatedProject = projectRepository.save(project);
            log.info("Project updated successfully - ID: {}", updatedProject.getProjectId());
            
            // Get owner information
            Users owner = userRepository.findById(updatedProject.getOwnerId()).orElse(null);
            ProjectDTO projectDTO = projectMapper.toDTO(updatedProject);
            
            // Set owner information
            if (DataUtils.notNull(owner)) {
                projectDTO.setOwnerEmail(owner.getEmail());
                projectDTO.setOwnerAvatarUrl(owner.getAvatarUrl());
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

            int permissionCheck = projectAccessService.checkUserPermissionLevel(projectId, SecurityUtils.getCurrentUserId());
            if (permissionCheck == Constants.Permission.DEN){
                log.error( ErrorCode.NOT_PROJECT_OWNER.name());
                throw BaseException.of(ErrorCode.NOT_PROJECT_OWNER, HttpStatus.FORBIDDEN);
            }
            Project project = getAndCheckProject(projectId);
            // Get owner information
            Users owner = userRepository.findById(project.getOwnerId()).orElse(null);
            ProjectDTO projectDTO = projectMapper.toDTO(project);
            
            if (DataUtils.notNull(owner)) {
                projectDTO.setOwnerEmail(owner.getEmail());
                projectDTO.setOwnerAvatarUrl(owner.getAvatarUrl());
            } else {
                ownerNotFoundForProject(projectId);
            }
            return projectDTO;
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching project: {}", projectId, e);
            throw BaseException.of(ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

      public ProjectDTO getProjectById(String projectId,
                              InputPasswordShare inputPasswordShare){
        log.info("Fetching shared project by ID: {}", projectId);

           Project project = getAndCheckProject(projectId);
          if (project.getVisibility().equals(Constants.Visibility.PRIVATE)) {
            // check access permission for a private project
              int permissionCheck = projectAccessService.checkUserPermissionLevel(projectId, SecurityUtils.getCurrentUserId());
              log.info("Permission check: {} & project private", permissionCheck);
              if (permissionCheck == Constants.Permission.DEN){
                  throw BaseException.of(ErrorCode.NOT_PROJECT_OWNER, HttpStatus.FORBIDDEN);
              }
          }
          Users owner = userRepository.findById(project.getOwnerId()).orElse(null);
          ProjectDTO projectDTO = projectMapper.toDTO(project);

          if (DataUtils.notNull(owner)) {
              projectDTO.setOwnerEmail(owner.getEmail());
              projectDTO.setOwnerAvatarUrl(owner.getAvatarUrl());
              log.debug("Added owner information - email: {}, avatarUrl: {}",
                      owner.getEmail(), owner.getAvatarUrl());
          } else {
              ownerNotFoundForProject(project.getProjectId());
          }
            // Check share password for a protected project
          if(Objects.equals( project.getVisibility(), Constants.Visibility.PROTECTED) && !passwordEncoder.matches(inputPasswordShare.getPasswordShare(), project.getPasswordShare())) {
              // check share password for a protected project
              log.error("Invalid share password for project: {}", projectId);
              throw BaseException.of(ErrorCode.FORBIDDEN, HttpStatus.FORBIDDEN);
          }
        return projectDTO;
    }

    @Override
    public List<ProjectDTO> getAllProjects() {
        String currentUserId = SecurityUtils.getCurrentUserId();
        log.info("Fetching all projects for user: {}", currentUserId);
        
        try {
            List<Project> projects = projectRepository.findByOwnerId(currentUserId);
            // Get current user information for setting owner details
            Users currentUser = userRepository.findById(currentUserId).orElse(null);
            
            return projects.stream()
                    .map(project -> {
                        ProjectDTO dto = projectMapper.toDTO(project);
                        // Since these are the user's own projects, set owner info from the current user
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
            Project project = getAndCheckProject(projectId);
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
            // fill accesses by the current user
            List<ProjectAccess> projectAccesses = projectAccessRepository.findByIdentifier(currentUserId);
            log.info("Found {} shared projects for user", projectAccesses.size());
            if (projectAccesses.isEmpty()) {
                return new ArrayList<>();
            }
            // get all project IDs from accesses
            List<String> projectIds = projectAccesses.stream()
                    .map(ProjectAccess::getProjectId)
                    .toList();
            // find all projects by IDs
            List<Project> projects = projectRepository.findAllById(projectIds);
            
            // filter projects to only those not owned by the current user
            List<Project> sharedProjects = projects.stream()
                    .filter(project -> !project.getOwnerId().equals(currentUserId))
                    .toList();
            
            log.info("Processing {} shared projects with owner information", sharedProjects.size());
            List<ProjectResponse> result = new ArrayList<>();
            for (Project project : sharedProjects) {
                // find owner information
                Users owner = userRepository.findById(project.getOwnerId()).orElse(null);
                if (owner != null) {
                    log.debug("Found owner: {} for project: {}", owner.getEmail(), project.getProjectId());
                } else {
                    log.warn("Owner not found for project: {}", project.getProjectId());
                }
                // create a response with owner information
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

    @Override
    public ShareDbDocsDto shareProject(String projectId, ShareDbDocsRequest shareDbDocsRequest) {
        log.info("Sharing project with ID: {}", projectId);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> {
                    log.error("Project not found with ID: {}", projectId);
                    return BaseException.of(ErrorCode.PROJECT_NOT_FOUND);
                });
        int permissionCheck = projectAccessService.checkUserPermissionLevel(projectId, SecurityUtils.getCurrentUserId());
        if (permissionCheck == Constants.Permission.DEN || permissionCheck == Constants.Permission.VIEWER) {
            log.error("Permission denied - user: {} does not have access to project: {}",
                    SecurityUtils.getCurrentUserId(), projectId);
            throw BaseException.of(ErrorCode.NOT_PROJECT_OWNER, HttpStatus.FORBIDDEN);
        }
        project.setVisibility(shareDbDocsRequest.getShareType());
        if( shareDbDocsRequest.getShareType() == Constants.Visibility.PROTECTED) {
            project.setPasswordShare(passwordEncoder.encode(shareDbDocsRequest.getPasswordShare()));
        }
        projectRepository.save(project);
        return ShareDbDocsDto.builder()
                .linkDocs(frontendUrl+ "/project/"+ shareDbDocsRequest.getShareType()+"/" + project.getProjectId()+"/docs")
                .build();
    }

    private Project getAndCheckProject(String projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> {
                    log.error(ErrorCode.PROJECT_NOT_FOUND.name());
                    return BaseException.of(ErrorCode.PROJECT_NOT_FOUND);
                });
        log.info("Project found: {}", project.getProjectCode());
        return project;
    }

    private void ownerNotFoundForProject(String projectId) {
        log.error("Owner not found for project: {}", projectId);
    }
}
