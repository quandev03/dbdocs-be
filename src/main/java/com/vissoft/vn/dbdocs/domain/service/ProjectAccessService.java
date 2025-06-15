package com.vissoft.vn.dbdocs.domain.service;

import java.util.List;

import com.vissoft.vn.dbdocs.application.dto.ProjectAccessDTO;
import com.vissoft.vn.dbdocs.application.dto.ProjectAccessRequest;
import com.vissoft.vn.dbdocs.application.dto.ProjectPermissionRequest;
import com.vissoft.vn.dbdocs.application.dto.ProjectVisibilityRequest;

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
     * Kiểm tra mức độ quyền của người dùng đối với dự án, chỉ dựa trên quyền rõ ràng
     * không quan tâm đến cài đặt visibility của dự án
     * 
     * @param projectId ID của dự án
     * @param userId ID của người dùng
     * @return 1: Owner (người tạo dự án), 2: View (quyền xem), 3: Edit (quyền sửa), 4: Denied (không có quyền)
     */
    Integer checkUserPermissionLevel(String projectId, String userId);
    
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