package com.vissoft.vn.dbdocs.domain.service;

import com.vissoft.vn.dbdocs.application.dto.DdlScriptRequest;
import com.vissoft.vn.dbdocs.application.dto.DdlScriptResponse;
import com.vissoft.vn.dbdocs.application.dto.SingleVersionDdlRequest;
import com.vissoft.vn.dbdocs.application.dto.VersionCreateRequest;
import com.vissoft.vn.dbdocs.application.dto.VersionDTO;

import java.util.List;

public interface VersionService {
    VersionDTO createVersion(VersionCreateRequest request);
    VersionDTO getVersionById(String versionId);
    List<VersionDTO> getVersionsByProjectId(String projectId);
    DdlScriptResponse generateDdlScript(DdlScriptRequest request);
    DdlScriptResponse generateSingleVersionDdl(SingleVersionDdlRequest request);
} 