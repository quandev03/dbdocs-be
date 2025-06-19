package com.vissoft.vn.dbdocs.domain.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.vissoft.vn.dbdocs.infrastructure.constant.Constants;
import com.vissoft.vn.dbdocs.infrastructure.util.DataUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vissoft.vn.dbdocs.application.dto.ProjectAccessDTO;
import com.vissoft.vn.dbdocs.application.dto.ProjectAccessRequest;
import com.vissoft.vn.dbdocs.application.dto.ProjectPermissionRequest;
import com.vissoft.vn.dbdocs.application.dto.ProjectVisibilityRequest;
import com.vissoft.vn.dbdocs.domain.entity.Project;
import com.vissoft.vn.dbdocs.domain.entity.ProjectAccess;
import com.vissoft.vn.dbdocs.domain.entity.Users;
import com.vissoft.vn.dbdocs.domain.repository.ProjectAccessRepository;
import com.vissoft.vn.dbdocs.domain.repository.ProjectRepository;
import com.vissoft.vn.dbdocs.domain.repository.UserRepository;
import com.vissoft.vn.dbdocs.domain.service.ProjectAccessService;
import com.vissoft.vn.dbdocs.infrastructure.exception.BaseException;
import com.vissoft.vn.dbdocs.infrastructure.exception.ErrorCode;
import com.vissoft.vn.dbdocs.infrastructure.mapper.ProjectAccessMapper;
import com.vissoft.vn.dbdocs.infrastructure.security.SecurityUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectAccessServiceImpl implements ProjectAccessService {
    private final ProjectAccessRepository projectAccessRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectAccessMapper projectAccessMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public ProjectAccessDTO addUserToProject(ProjectAccessRequest request) {
        log.info("Adding user to project - projectId: {}, email/username: {}, by userId: {}", 
                request.getProjectId(), request.getEmailOrUsername(), SecurityUtils.getCurrentUserId());
        try {
            // Check if the project exists and this user is the owner
            Project project = getAndCheckProjectById(request.getProjectId());
            if (!Objects.equals(project.getOwnerId(), SecurityUtils.getCurrentUserId())) {
                log.error(ErrorCode.NOT_PROJECT_OWNER.name());
                throw BaseException.of(ErrorCode.NOT_PROJECT_OWNER, HttpStatus.FORBIDDEN);
            }

            // Find information user by email or username
            Users user = getAndCheckUserByEmail(request.getEmailOrUsername());

            // Check if this current user already has access to the project
            if (projectAccessRepository.findByProjectIdAndIdentifier(request.getProjectId(), user.getUserId()).isPresent()) {
                log.error("User already has access - socialId: {}, projectId: {}", 
                        user.getUserId(), request.getProjectId());
                throw BaseException.of(ErrorCode.USER_ALREADY_HAS_ACCESS);
            }
            
            // Create a new ProjectAccess entry
            ProjectAccess projectAccess = new ProjectAccess();
            projectAccess.setProjectId(request.getProjectId());
            projectAccess.setIdentifier(user.getUserId());
            projectAccess.setPermission(request.getPermission());
            projectAccess.setOwnerId(project.getOwnerId());
            
            ProjectAccess savedAccess = projectAccessRepository.save(projectAccess);
            log.info("User successfully added to project - socialId: {}, projectId: {}, permission: {}", 
                    user.getSocialId(), request.getProjectId(), request.getPermission());
            return projectAccessMapper.toDTO(savedAccess);
        } catch (BaseException e) {
            log.error("Error adding user to project", e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error adding user to project", e);
            throw BaseException.of(ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public void changeProjectVisibility(ProjectVisibilityRequest request) {
        String currentUserId = SecurityUtils.getCurrentUserId();
        log.info("Changing project visibility - projectId: {}, visibility: {}, by userId: {}", 
                request.getProjectId(), request.getVisibility(), currentUserId);
        
        try {
            // Get information project by ID and check ownership
            Project project = getAndCheckProjectById(request.getProjectId());
            //Check if the current user is the owner of the project
            if (!Objects.equals(project.getOwnerId(), currentUserId)) {
                log.error(ErrorCode.NOT_PROJECT_OWNER.name());
                throw BaseException.of(ErrorCode.NOT_PROJECT_OWNER, HttpStatus.FORBIDDEN);
            }
            // Check the visibility type
            if (
                    request.getVisibility() < Constants.Visibility.PUBLIC ||
                    request.getVisibility() > Constants.Visibility.PROTECTED
            ) {
                log.error("Invalid visibility type: {}", request.getVisibility());
                throw BaseException.of(ErrorCode.INVALID_VISIBILITY_TYPE);
            }
            // Set password for protected visibility
            if (Objects.equals(request.getVisibility(), Constants.Visibility.PROTECTED)) {
                if (DataUtils.isNull(request.getPassword())) {
                    log.error("Password required for protected visibility");
                    throw BaseException.of(ErrorCode.PASSWORD_REQUIRED);
                }
                project.setPasswordShare(passwordEncoder.encode(request.getPassword()));
                log.info("Password set for protected project");
            } else {
                project.setPasswordShare(null);
                log.info("Password removed as project is not protected");
            }
            
            int oldVisibility = project.getVisibility();
            project.setVisibility(request.getVisibility());
            projectRepository.save(project);
            
            log.info("Project visibility changed from {} to {} - projectId: {}", 
                    oldVisibility, request.getVisibility(), request.getProjectId());
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error changing project visibility", e);
            throw BaseException.of(ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Integer checkUserAccess(String projectId, String userId) {
        log.info("Checking user access - projectId: {}, userId: {}", projectId, userId);
        
        try {
            Project project = getAndCheckProjectById(projectId);
            
            // Check the project exists
            Users user = getAndCheckUserById(userId);
            
            // If the user is the owner of the project, grant EDIT permission
            if (project.getOwnerId().equals(userId)) {
                log.info("User is project owner - granted EDIT permission");
                return Constants.Permission.OWNER; // Edit permission
            }

            // Check if the project is public
            Integer permission = projectAccessRepository.findByProjectIdAndIdentifier(projectId, user.getUserId())
                    .map(ProjectAccess::getPermission)
                    .orElse(null);
            
            if (DataUtils.notNull(permission)) {
                log.info("User has explicit permission - projectId: {}, permission: {}",
                        projectId, permission);
            } else {
                return Constants.Permission.DEN;
            }
            return permission;
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error checking user access", e);
            throw BaseException.of(ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public ProjectAccessDTO changeUserPermission(ProjectPermissionRequest request) {
        String currentUserId = SecurityUtils.getCurrentUserId();
        log.info("Changing user permission - projectId: {}, identifier: {}, permission: {}, by userId: {}", 
                request.getProjectId(), request.getIdentifier(), request.getPermission(), currentUserId);
        try {
            // Check if a project exists and the user is the owner
            Project project = getAndCheckProjectById(request.getProjectId());

            // Check if the current user is the owner of the project
            if (!Objects.equals(project.getOwnerId(), currentUserId)) {
                log.error(ErrorCode.NOT_PROJECT_OWNER.name());
                throw BaseException.of(ErrorCode.NOT_PROJECT_OWNER, HttpStatus.FORBIDDEN);
            }
            
            // Check if the user exists
            ProjectAccess projectAccess = projectAccessRepository.findByProjectIdAndIdentifier(
                    request.getProjectId(), request.getIdentifier())
                    .orElseThrow(() -> {
                        log.error("User does not have access - identifier: {}, projectId: {}", 
                                request.getIdentifier(), request.getProjectId());
                        return BaseException.of(ErrorCode.USER_DOES_NOT_HAVE_ACCESS);
                    });
            
            // Change the permission level
            int oldPermission = projectAccess.getPermission();
            projectAccess.setPermission(request.getPermission());
            ProjectAccess savedAccess = projectAccessRepository.save(projectAccess);
            
            log.info("User permission changed from {} to {} - identifier: {}, projectId: {}", 
                    oldPermission, request.getPermission(), request.getIdentifier(), request.getProjectId());
            
            // Find the user by socialId or userId
            Users user = getAndCheckUserById(projectAccess.getIdentifier());
            return projectAccessMapper.toDTOWithUser(savedAccess, user);
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error changing user permission", e);
            throw BaseException.of(ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public void removeUserFromProject(String projectId, String identifier) {
        String currentUserId = SecurityUtils.getCurrentUserId();
        log.info("Removing user from project - projectId: {}, identifier: {}, by userId: {}", 
                projectId, identifier, currentUserId);
        
        try {
            // check if a project exists and the user is the owner
            Project project = getAndCheckProjectById(projectId);
            if (!Objects.equals(project.getOwnerId(), currentUserId)) {
                log.error("Permission denied - user: {} is not owner of project: {}", 
                        currentUserId, projectId);
                throw BaseException.of(ErrorCode.NOT_PROJECT_OWNER, HttpStatus.FORBIDDEN);
            }

            // Delete the project access entry
            projectAccessRepository.deleteByProjectIdAndIdentifier(projectId, identifier);
            log.info("User successfully removed from project - identifier: {}, projectId: {}", 
                    identifier, projectId);
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error removing user from project", e);
            throw BaseException.of(ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<ProjectAccessDTO> getUsersWithAccessToProject(String projectId) {
        String currentUserId = SecurityUtils.getCurrentUserId();
        log.info("Getting users with access to project - projectId: {}, by userId: {}", 
                projectId, currentUserId);
        try {
            if(!projectRepository.existsByProjectId(projectId)){
                log.error(ErrorCode.PROJECT_NOT_FOUND.name());
                throw  BaseException.of(ErrorCode.PROJECT_NOT_FOUND);
            }
            // Check if the current user has access to the project
            Integer permission = checkUserAccess(projectId, currentUserId);
            if (DataUtils.isNull(permission)) {
                log.error("Access denied - user: {} does not have permission to view project: {}", 
                        currentUserId, projectId);
                throw BaseException.of(ErrorCode.PROJECT_ACCESS_DENIED, HttpStatus.FORBIDDEN);
            }
            
            // Get the list of users with access to the project
            List<ProjectAccess> accessList = projectAccessRepository.findByProjectId(projectId);
            List<ProjectAccessDTO> result = new ArrayList<>();
            
            log.info("Found {} users with access to project: {}", accessList.size(), projectId);
            for (ProjectAccess access : accessList) {
                Users user = getAndCheckUserById(access.getIdentifier());
                result.add(projectAccessMapper.toDTOWithUser(access, user));
            }
            
            // Check if any users were found
            log.info("Returning {} project access DTOs", result.size());
            for (int i = 0; i < Math.min(result.size(), 5); i++) { // Log tối đa 5 kết quả đầu tiên
                ProjectAccessDTO dto = result.get(i);
                log.info("Access DTO {} - id: {}, identifier: {}, userEmail: {}, userName: {}, avatarUrl: {}", 
                        i, dto.getId(), dto.getIdentifier(), dto.getUserEmail(), dto.getUserName(), dto.getAvatarUrl());
            }
            return result;
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error getting users with access to project", e);
            throw BaseException.of(ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Integer checkUserPermissionLevel(String projectId, String userId) {
        log.info("Checking user permission level - projectId: {}, userId: {}", projectId, userId);
        
        try {
            Project project =getAndCheckProjectById(projectId);
            Users user = getAndCheckUserById(userId);
            
            // If the user is the owner of the project, return owner permission level
            if (Objects.equals(project.getOwnerId(), userId)) {
                log.info("User is project owner - permission level: 1 (Owner)");
                return Constants.Permission.OWNER;
            }
            
            // Check if the user has explicit permission in the project access table
            Integer permission = projectAccessRepository.findByProjectIdAndIdentifier(projectId, user.getUserId())
                    .map(ProjectAccess::getPermission)
                    .orElse(null);
            
            // Check if the user has explicit permission
            if (DataUtils.notNull(permission)) {
                log.info("User has explicit permission level: {} for project: {}", 
                        Objects.equals(permission, Constants.Permission.VIEWER) ? "View" : "Edit", projectId);
                return permission;
            }
            
            // Not accessed explicitly, check if the project is public
            log.info("User has no access to project: {} - permission level: 4 (Denied)", projectId);
            return Constants.Permission.DEN;
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error checking user permission level", e);
            throw BaseException.of(ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    private Project getAndCheckProjectById(String projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> {
                    log.error(ErrorCode.PROJECT_NOT_FOUND.name());
                    return BaseException.of(ErrorCode.PROJECT_NOT_FOUND);
                });
    }
    private Users getAndCheckUserById(String userId){
        Users users = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error(ErrorCode.USER_NOT_FOUND.name());
                    return BaseException.of(ErrorCode.USER_NOT_FOUND);
                });
        log.info("Found user: {} with userId: {}", userId, userId);
        return users;
    }
    private Users getAndCheckUserByEmail(String email) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error(ErrorCode.USER_NOT_FOUND.name());
                    return BaseException.of(ErrorCode.USER_NOT_FOUND);
                });
        log.info("Found user: {} with email: {}", user.getUserId(), email);
        return user;
    }
} 