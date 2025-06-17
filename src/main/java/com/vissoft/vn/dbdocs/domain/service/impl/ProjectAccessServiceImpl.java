package com.vissoft.vn.dbdocs.domain.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.vissoft.vn.dbdocs.infrastructure.constant.Constants;
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
    private final SecurityUtils securityUtils;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public ProjectAccessDTO addUserToProject(ProjectAccessRequest request) {
        String currentUserId = securityUtils.getCurrentUserId();
        log.info("Adding user to project - projectId: {}, email/username: {}, by userId: {}", 
                request.getProjectId(), request.getEmailOrUsername(), currentUserId);
        
        try {
            // Kiểm tra dự án tồn tại và người dùng hiện tại có quyền sở hữu
            Project project = projectRepository.findById(request.getProjectId())
                    .orElseThrow(() -> {
                        log.error("Project not found - projectId: {}", request.getProjectId());
                        return BaseException.of(ErrorCode.PROJECT_NOT_FOUND);
                    });
            
            if (!project.getOwnerId().equals(currentUserId)) {
                log.error("Permission denied - user: {} is not owner of project: {}", 
                        currentUserId, request.getProjectId());
                throw BaseException.of(ErrorCode.NOT_PROJECT_OWNER, HttpStatus.FORBIDDEN);
            }
            
            // Tìm người dùng theo email
            Users user = userRepository.findByEmail(request.getEmailOrUsername())
                    .orElseThrow(() -> {
                        log.error("User not found with email/username: {}", request.getEmailOrUsername());
                        return BaseException.of(ErrorCode.USER_NOT_FOUND);
                    });
            
            log.info("Found user: {} with socialId: {}", user.getEmail(), user.getUserId());
            
            // Kiểm tra người dùng đã được thêm vào dự án chưa
            if (projectAccessRepository.findByProjectIdAndIdentifier(request.getProjectId(), user.getUserId()).isPresent()) {
                log.error("User already has access - socialId: {}, projectId: {}", 
                        user.getUserId(), request.getProjectId());
                throw BaseException.of(ErrorCode.USER_ALREADY_HAS_ACCESS);
            }
            
            // Thêm người dùng vào dự án
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
            // Rethrow BaseException as it already contains error information
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error adding user to project", e);
            throw BaseException.of(ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public void changeProjectVisibility(ProjectVisibilityRequest request) {
        String currentUserId = securityUtils.getCurrentUserId();
        log.info("Changing project visibility - projectId: {}, visibility: {}, by userId: {}", 
                request.getProjectId(), request.getVisibility(), currentUserId);
        
        try {
            // Kiểm tra dự án tồn tại và người dùng hiện tại có quyền sở hữu
            Project project = projectRepository.findById(request.getProjectId())
                    .orElseThrow(() -> {
                        log.error("Project not found - projectId: {}", request.getProjectId());
                        return BaseException.of(ErrorCode.PROJECT_NOT_FOUND);
                    });
            
            if (!project.getOwnerId().equals(currentUserId)) {
                log.error("Permission denied - user: {} is not owner of project: {}", 
                        currentUserId, request.getProjectId());
                throw BaseException.of(ErrorCode.NOT_PROJECT_OWNER, HttpStatus.FORBIDDEN);
            }
            
            // Kiểm tra loại visibility
            if (request.getVisibility() < 1 || request.getVisibility() > 3) {
                log.error("Invalid visibility type: {}", request.getVisibility());
                throw BaseException.of(ErrorCode.INVALID_VISIBILITY_TYPE);
            }
            
            // Nếu là protected, kiểm tra mật khẩu
            if (request.getVisibility() == 3) {
                if (request.getPassword() == null || request.getPassword().isEmpty()) {
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
            // Kiểm tra dự án tồn tại
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> {
                        log.error("Project not found - projectId: {}", projectId);
                        return BaseException.of(ErrorCode.PROJECT_NOT_FOUND);
                    });
            
            // Kiểm tra người dùng
            Users user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.error("User not found - userId: {}", userId);
                        return BaseException.of(ErrorCode.USER_NOT_FOUND);
                    });
            
            // Nếu là chủ sở hữu, có quyền edit
            if (project.getOwnerId().equals(userId)) {
                log.info("User is project owner - granted EDIT permission");
                return 2; // Edit permission
            }
            
            // Nếu là public, có quyền view
            if (project.getVisibility() == 1) {
                log.info("Project is public - granted VIEW permission");
                return 1; // View permission
            }
            
            // Nếu là private hoặc protected, kiểm tra trong bảng access
            Integer permission = projectAccessRepository.findByProjectIdAndIdentifier(projectId, user.getUserId())
                    .map(ProjectAccess::getPermission)
                    .orElse(null);
            
            if (permission != null) {
                log.info("User has explicit permission: {} for project: {}", 
                        permission == Constants.Permission.OWNER ? "OWNER" : permission == Constants.Permission.VIEWER ? "VIEW" :"EDIT", projectId);
            } else {
                log.info("User has no access to project: {}", projectId);
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
        String currentUserId = securityUtils.getCurrentUserId();
        log.info("Changing user permission - projectId: {}, identifier: {}, permission: {}, by userId: {}", 
                request.getProjectId(), request.getIdentifier(), request.getPermission(), currentUserId);
        
        try {
            // Kiểm tra dự án tồn tại và người dùng hiện tại có quyền sở hữu
            Project project = projectRepository.findById(request.getProjectId())
                    .orElseThrow(() -> {
                        log.error("Project not found - projectId: {}", request.getProjectId());
                        return BaseException.of(ErrorCode.PROJECT_NOT_FOUND);
                    });
            
            if (!project.getOwnerId().equals(currentUserId)) {
                log.error("Permission denied - user: {} is not owner of project: {}", 
                        currentUserId, request.getProjectId());
                throw BaseException.of(ErrorCode.NOT_PROJECT_OWNER, HttpStatus.FORBIDDEN);
            }
            
            // Kiểm tra người dùng đã được thêm vào dự án chưa
            ProjectAccess projectAccess = projectAccessRepository.findByProjectIdAndIdentifier(
                    request.getProjectId(), request.getIdentifier())
                    .orElseThrow(() -> {
                        log.error("User does not have access - identifier: {}, projectId: {}", 
                                request.getIdentifier(), request.getProjectId());
                        return BaseException.of(ErrorCode.USER_DOES_NOT_HAVE_ACCESS);
                    });
            
            // Thay đổi quyền
            int oldPermission = projectAccess.getPermission();
            projectAccess.setPermission(request.getPermission());
            ProjectAccess savedAccess = projectAccessRepository.save(projectAccess);
            
            log.info("User permission changed from {} to {} - identifier: {}, projectId: {}", 
                    oldPermission, request.getPermission(), request.getIdentifier(), request.getProjectId());
            
            // Tìm thông tin người dùng
            Users user = userRepository.findBySocialId(request.getIdentifier()).orElse(null);
            
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
        String currentUserId = securityUtils.getCurrentUserId();
        log.info("Removing user from project - projectId: {}, identifier: {}, by userId: {}", 
                projectId, identifier, currentUserId);
        
        try {
            // Kiểm tra dự án tồn tại và người dùng hiện tại có quyền sở hữu
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> {
                        log.error("Project not found - projectId: {}", projectId);
                        return BaseException.of(ErrorCode.PROJECT_NOT_FOUND);
                    });
            
            if (!project.getOwnerId().equals(currentUserId)) {
                log.error("Permission denied - user: {} is not owner of project: {}", 
                        currentUserId, projectId);
                throw BaseException.of(ErrorCode.NOT_PROJECT_OWNER, HttpStatus.FORBIDDEN);
            }
            
            // Xóa người dùng khỏi dự án
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
        String currentUserId = securityUtils.getCurrentUserId();
        log.info("Getting users with access to project - projectId: {}, by userId: {}", 
                projectId, currentUserId);
        
        try {
            // Kiểm tra dự án tồn tại
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> {
                        log.error("Project not found - projectId: {}", projectId);
                        return BaseException.of(ErrorCode.PROJECT_NOT_FOUND);
                    });
            
            // Kiểm tra người dùng hiện tại có quyền xem
            Integer permission = checkUserAccess(projectId, currentUserId);
            if (permission == null) {
                log.error("Access denied - user: {} does not have permission to view project: {}", 
                        currentUserId, projectId);
                throw BaseException.of(ErrorCode.PROJECT_ACCESS_DENIED, HttpStatus.FORBIDDEN);
            }
            
            // Lấy danh sách người dùng có quyền truy cập
            List<ProjectAccess> accessList = projectAccessRepository.findByProjectId(projectId);
            List<ProjectAccessDTO> result = new ArrayList<>();
            
            log.info("Found {} users with access to project: {}", accessList.size(), projectId);
            
            for (ProjectAccess access : accessList) {
                // Trước tiên thử tìm theo socialId
                Users user = userRepository.findBySocialId(access.getIdentifier()).orElse(null);
                
                // Nếu không tìm thấy, thử tìm theo userId
                if (user == null) {
                    log.info("User not found by socialId: {}, trying to find by userId", access.getIdentifier());
                    user = userRepository.findById(access.getIdentifier()).orElse(null);
                }
                
                if (user != null) {
                    log.info("Found user for access - identifier: {}, email: {}, fullName: {}, avatarUrl: {}", 
                            access.getIdentifier(), user.getEmail(), user.getFullName(), user.getAvatarUrl());
                } else {
                    log.warn("User not found for identifier: {} in project: {}", 
                            access.getIdentifier(), projectId);
                }
                
                result.add(projectAccessMapper.toDTOWithUser(access, user));
            }
            
            // Kiểm tra kết quả cuối cùng
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
    
    /**
     * Phương thức này tự động thêm owner vào danh sách access khi tạo project
     */
    @Transactional
    public void addOwnerToProject(String projectId, String ownerId) {
        log.info("Adding owner to project access - projectId: {}, ownerId: {}", projectId, ownerId);
        
        try {
            // Tìm user thông tin owner
            Users owner = userRepository.findById(ownerId)
                    .orElseThrow(() -> {
                        log.error("Owner not found - ownerId: {}", ownerId);
                        return BaseException.of(ErrorCode.USER_NOT_FOUND);
                    });
            
            // Tạo access record cho owner với quyền edit (2)
            ProjectAccess ownerAccess = new ProjectAccess();
            ownerAccess.setProjectId(projectId);
            ownerAccess.setIdentifier(owner.getSocialId());
            ownerAccess.setPermission(2); // Edit permission
            ownerAccess.setOwnerId(ownerId);
            
            projectAccessRepository.save(ownerAccess);
            log.info("Owner successfully added to project access - socialId: {}, projectId: {}", 
                    owner.getSocialId(), projectId);
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error adding owner to project", e);
            throw BaseException.of(ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Integer checkUserPermissionLevel(String projectId, String userId) {
        log.info("Checking user permission level - projectId: {}, userId: {}", projectId, userId);
        
        try {
            // Kiểm tra dự án tồn tại
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> {
                        log.error("Project not found - projectId: {}", projectId);
                        return BaseException.of(ErrorCode.PROJECT_NOT_FOUND);
                    });
            
            // Kiểm tra người dùng
            Users user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.error("User not found - userId: {}", userId);
                        return BaseException.of(ErrorCode.USER_NOT_FOUND);
                    });
            
            // Nếu là chủ sở hữu, có quyền owner (1)
            if (project.getOwnerId().equals(userId)) {
                log.info("User is project owner - permission level: 1 (Owner)");
                return 1; // Owner permission
            }
            
            // Kiểm tra quyền trong bảng access
            Integer permission = projectAccessRepository.findByProjectIdAndIdentifier(projectId, user.getUserId())
                    .map(ProjectAccess::getPermission)
                    .orElse(null);
            
            // Nếu có quyền trong bảng access
            if (permission != null) {
                // Chuyển đổi permission từ cũ sang mới
                // Cũ: 1 = View, 2 = Edit
                // Mới: 2 = View, 3 = Edit
                int permissionLevel = permission == 1 ? 2 : 3; // 1->2 (View), 2->3 (Edit)
                log.info("User has explicit permission level: {} for project: {}", 
                        permissionLevel == 2 ? "View" : "Edit", projectId);
                return permissionLevel;
            }
            
            // Không có quyền truy cập
            log.info("User has no access to project: {} - permission level: 4 (Denied)", projectId);
            return 4; // Denied
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error checking user permission level", e);
            throw BaseException.of(ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
} 