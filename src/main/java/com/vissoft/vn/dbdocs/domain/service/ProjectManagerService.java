package com.vissoft.vn.dbdocs.domain.service;

import com.vissoft.vn.dbdocs.application.dto.ProjectCreateRequest;
import com.vissoft.vn.dbdocs.application.dto.ProjectDTO;
import com.vissoft.vn.dbdocs.application.dto.ProjectResponse;
import com.vissoft.vn.dbdocs.application.dto.ProjectUpdateRequest;
import com.vissoft.vn.dbdocs.interfaces.rest.dto.InputPasswordShare;
import com.vissoft.vn.dbdocs.interfaces.rest.dto.ShareDbDocsDto;
import com.vissoft.vn.dbdocs.interfaces.rest.dto.ShareDbDocsRequest;

import java.util.List;

public interface ProjectManagerService {

    /**
     * Tạo mới một dự án
     *
     * @param request Thông tin dự án cần tạo
     * @return Dự án đã được tạo
     */
    ProjectDTO createProject(ProjectCreateRequest request);

    /**
     * Cập nhật thông tin dự án
     *
     * @param projectId ID của dự án cần cập nhật
     * @param request Thông tin cập nhật dự án
     * @return Dự án đã được cập nhật
     */
    ProjectDTO updateProject(String projectId, ProjectUpdateRequest request);

    /**
     * Lấy thông tin dự án theo ID
     *
     * @param projectId ID của dự án cần lấy thông tin
     * @return Dự án tương ứng với ID
     */
    ProjectDTO getProjectById(String projectId);



    /**
     * Lấy thông tin dự án theo ID và kiểm tra quyền truy cập
     *
     * @param projectId ID của dự án cần lấy thông tin
     * @param inputPasswordShare Thông tin xác thực để chia sẻ dự án
     * @return Dự án tương ứng với ID nếu có quyền truy cập, null nếu không có quyền
     */
    ProjectDTO getProjectById(String projectId,
                              InputPasswordShare inputPasswordShare);


    /**     * Lấy danh sách tất cả các dự án
     *
     * @return Danh sách các dự án
     */
    List<ProjectDTO> getAllProjects();

    /**
     * Xóa một dự án theo ID
     *
     * @param projectId ID của dự án cần xóa
     */
    void deleteProject(String projectId);


    
    /**
     * Lấy danh sách các project được chia sẻ với user hiện tại
     * @return Danh sách các project được chia sẻ kèm theo thông tin owner
     */
    List<ProjectResponse> getSharedProjects();

    /**
     * Chia sẻ dự án với người dùng khác
     * @param projectId ID của dự án cần chia sẻ
     * @param shareDbDocsRequest Thông tin chia sẻ dự án
     */
    ShareDbDocsDto shareProject(String projectId, ShareDbDocsRequest shareDbDocsRequest);
}
