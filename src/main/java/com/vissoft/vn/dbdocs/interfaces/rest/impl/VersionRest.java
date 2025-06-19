package com.vissoft.vn.dbdocs.interfaces.rest.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vissoft.vn.dbdocs.application.dto.ChangeLogDdlRequest;
import com.vissoft.vn.dbdocs.application.dto.DdlScriptRequest;
import com.vissoft.vn.dbdocs.application.dto.DdlScriptResponse;
import com.vissoft.vn.dbdocs.application.dto.SingleVersionDdlRequest;
import com.vissoft.vn.dbdocs.application.dto.VersionCreateRequest;
import com.vissoft.vn.dbdocs.application.dto.VersionDTO;
import com.vissoft.vn.dbdocs.domain.service.VersionComparisonService;
import com.vissoft.vn.dbdocs.domain.service.VersionService;
import com.vissoft.vn.dbdocs.interfaces.rest.VersionOperator;
import com.vissoft.vn.dbdocs.interfaces.rest.dto.CompareCodeResponse;
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
    private final ObjectMapper objectMapper;

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

    /**
     * Compares two versions of a DBML schema for the specified project.
     * If beforeVersion is null, it will use the previous version.
     * If currentVersion is null, it will use the latest version.
     *
     * @param projectId The ID of the project
     * @param beforeVersion The version code of the earlier version (can be null)
     * @param currentVersion The version code of the later version (can be null)
     * @return ResponseEntity containing the comparison results
     * @throws JsonProcessingException If there's an error processing the JSON
     */
    @Override
    public ResponseEntity<CompareCodeResponse> compareVersions(
            String projectId, Integer beforeVersion, Integer currentVersion) throws JsonProcessingException {
        log.info("REST request to compare versions for project: {}, from: {}, to: {}", 
                projectId, beforeVersion, currentVersion);
        // Convert the diff changes to JSON string
        String diffChanges = objectMapper.writeValueAsString(
                versionComparisonService.compareVersions(projectId, beforeVersion, currentVersion)
        );
        return ResponseEntity.ok(new CompareCodeResponse(diffChanges));
    }

    /**
     * Generates a DDL script for the specified project and version range.
     *
     * @param request The request containing project ID, version range, and dialect.
     * @return ResponseEntity containing the generated DDL script.
     */
    @Override
    public ResponseEntity<DdlScriptResponse> generateDdlScript(DdlScriptRequest request) {
        log.info("REST request to generate DDL script for project: {}, from version: {}, to version: {}, dialect: {}", 
                request.getProjectId(), request.getFromVersion(), request.getToVersion(), request.getDialect());
        return ResponseEntity.ok(versionService.generateDdlScript(request));
    }

    /**
     * Generates a DDL script for a single version of the specified project.
     *
     * @param request The request containing project ID, version number, and dialect.
     * @return ResponseEntity containing the generated DDL script.
     */
    @Override
    public ResponseEntity<DdlScriptResponse> generateSingleVersionDdl(SingleVersionDdlRequest request) {
        log.info("REST request to generate DDL script for single version - Project: {}, Version: {}, Dialect: {}", 
                request.getProjectId(), request.getVersionNumber(), request.getDialect());
        return ResponseEntity.ok(versionService.generateSingleVersionDdl(request));
    }

    /**
     * Generates a DDL script from the change log for the specified project.
     *
     * @param request The request containing project ID, change log code, and dialect.
     * @return ResponseEntity containing the generated DDL script.
     */
    @Override
    public ResponseEntity<DdlScriptResponse> generateChangeLogDdl(ChangeLogDdlRequest request) {
        log.info("REST request to generate DDL script from changelog - ProjectID: {}, ChangeLogCode: {}, Dialect: {}", 
                request.getProjectId(), request.getChangeLogCode(), request.getDialect());

        DdlScriptResponse response = versionService.generateChangeLogDdl(request);
        return ResponseEntity.ok(response);
    }
} 
