package com.vissoft.vn.dbdocs.interfaces.rest.impl;

import com.vissoft.vn.dbdocs.application.dto.DdlScriptRequest;
import com.vissoft.vn.dbdocs.application.dto.DdlScriptResponse;
import com.vissoft.vn.dbdocs.application.dto.SingleVersionDdlRequest;
import com.vissoft.vn.dbdocs.application.dto.VersionComparisonDTO;
import com.vissoft.vn.dbdocs.application.dto.VersionCreateRequest;
import com.vissoft.vn.dbdocs.application.dto.VersionDTO;
import com.vissoft.vn.dbdocs.domain.service.VersionComparisonService;
import com.vissoft.vn.dbdocs.domain.service.VersionService;
import com.vissoft.vn.dbdocs.interfaces.rest.VersionOperator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class VersionRest implements VersionOperator {

    private final VersionService versionService;
    private final VersionComparisonService versionComparisonService;

    @Override
    public ResponseEntity<VersionDTO> createVersion(VersionCreateRequest request) {
        log.info("REST request to create version for project: {}", request.getProjectId());
        return ResponseEntity.ok(versionService.createVersion(request));
    }

    @Override
    public ResponseEntity<List<VersionDTO>> getVersionsByProjectId(String projectId) {
        log.info("REST request to get all versions for project: {}", projectId);
        return ResponseEntity.ok(versionService.getVersionsByProjectId(projectId));
    }

    @Override
    public ResponseEntity<VersionDTO> getVersionById(String versionId) {
        log.info("REST request to get version: {}", versionId);
        return ResponseEntity.ok(versionService.getVersionById(versionId));
    }
    
    @Override
    public ResponseEntity<VersionComparisonDTO> compareVersions(
            String projectId, Integer beforeVersion, Integer currentVersion) {
        log.info("REST request to compare versions for project: {}, from: {}, to: {}", 
                projectId, beforeVersion, currentVersion);
        return ResponseEntity.ok(versionComparisonService.compareVersions(projectId, beforeVersion, currentVersion));
    }
    
    @Override
    public ResponseEntity<DdlScriptResponse> generateDdlScript(DdlScriptRequest request) {
        log.info("REST request to generate DDL script for project: {}, from version: {}, to version: {}, dialect: {}", 
                request.getProjectId(), request.getFromVersion(), request.getToVersion(), request.getDialect());
        return ResponseEntity.ok(versionService.generateDdlScript(request));
    }
    
    @Override
    public ResponseEntity<DdlScriptResponse> generateSingleVersionDdl(SingleVersionDdlRequest request) {
        log.info("REST request to generate DDL script for single version - Project: {}, Version: {}, Dialect: {}", 
                request.getProjectId(), request.getVersionNumber(), request.getDialect());
        return ResponseEntity.ok(versionService.generateSingleVersionDdl(request));
    }
} 