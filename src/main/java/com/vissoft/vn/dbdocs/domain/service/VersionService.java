package com.vissoft.vn.dbdocs.domain.service;

import java.util.List;

import com.vissoft.vn.dbdocs.application.dto.ChangeLogDdlRequest;
import com.vissoft.vn.dbdocs.application.dto.DdlScriptRequest;
import com.vissoft.vn.dbdocs.application.dto.DdlScriptResponse;
import com.vissoft.vn.dbdocs.application.dto.SingleVersionDdlRequest;
import com.vissoft.vn.dbdocs.application.dto.VersionCreateRequest;
import com.vissoft.vn.dbdocs.application.dto.VersionDTO;

public interface VersionService {
    VersionDTO createVersion(VersionCreateRequest request);
    VersionDTO getVersionById(String versionId);
    List<VersionDTO> getVersionsByProjectId(String projectId);
    /**
     * Get latest version of a project including its changelog information.
     *
     * @param projectId the id of project.
     * @return VersionDTO representing latest version.
     */
    VersionDTO getLatestVersionByProjectId(String projectId);
    DdlScriptResponse generateDdlScript(DdlScriptRequest request);
    DdlScriptResponse generateSingleVersionDdl(SingleVersionDdlRequest request);
    DdlScriptResponse generateChangeLogDdl(ChangeLogDdlRequest request);
} 