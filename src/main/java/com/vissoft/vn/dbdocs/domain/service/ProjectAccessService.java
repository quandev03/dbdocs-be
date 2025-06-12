package com.vissoft.vn.dbdocs.domain.service;

import com.vissoft.vn.dbdocs.application.dto.ProjectAccessDTO;
import com.vissoft.vn.dbdocs.application.dto.ProjectAccessRequest;
import com.vissoft.vn.dbdocs.application.dto.ProjectPermissionRequest;
import com.vissoft.vn.dbdocs.application.dto.ProjectVisibilityRequest;

import java.util.List;

public interface ProjectAccessService {
    /**
     * Thêm người dùng vào dự án
     */
    ProjectAccessDTO addUserToProject(ProjectAccessRequest request);
    
    /**
     * Thay đổi kiểu chia sẻ dự án
     */
    void changeProjectVisibility(ProjectVisibilityRequest request);
    
    /**
     * Kiểm tra quyền truy cập của người dùng vào dự án
     * @return permission nếu có quyền, null nếu không có quyền
     */
    Integer checkUserAccess(String projectId, String userId);
    
    /**
     * Thay đổi quyền của người dùng trong dự án
     */
    ProjectAccessDTO changeUserPermission(ProjectPermissionRequest request);
    
    /**
     * Xóa người dùng khỏi dự án
     */
    void removeUserFromProject(String projectId, String identifier);
    
    /**
     * Lấy danh sách người dùng có quyền truy cập vào dự án
     */
    List<ProjectAccessDTO> getUsersWithAccessToProject(String projectId);
    
    /**
     * Thêm owner vào danh sách người có quyền truy cập dự án
     */
    void addOwnerToProject(String projectId, String ownerId);
} 