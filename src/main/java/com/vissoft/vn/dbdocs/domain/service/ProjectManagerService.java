package com.vissoft.vn.dbdocs.domain.service;

import com.vissoft.vn.dbdocs.application.dto.ProjectCreateRequest;
import com.vissoft.vn.dbdocs.application.dto.ProjectDTO;
import com.vissoft.vn.dbdocs.application.dto.ProjectResponse;
import com.vissoft.vn.dbdocs.application.dto.ProjectUpdateRequest;

import java.util.List;

public interface ProjectManagerService {
    ProjectDTO createProject(ProjectCreateRequest request);
    ProjectDTO updateProject(String projectId, ProjectUpdateRequest request);
    ProjectDTO getProjectById(String projectId);
    List<ProjectDTO> getAllProjects();
    void deleteProject(String projectId);
    
    /**
     * Lấy danh sách các project được chia sẻ với user hiện tại
     * @return Danh sách các project được chia sẻ kèm theo thông tin owner
     */
    List<ProjectResponse> getSharedProjects();
}
