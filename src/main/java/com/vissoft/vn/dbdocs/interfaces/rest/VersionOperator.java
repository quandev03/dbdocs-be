package com.vissoft.vn.dbdocs.interfaces.rest;

import java.util.List;

import com.vissoft.vn.dbdocs.application.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("/api/v1/versions")
public interface VersionOperator {

    @PostMapping
    ResponseEntity<VersionDTO> createVersion(@RequestBody VersionCreateRequest request);

    @GetMapping("/project/{projectId}")
    ResponseEntity<List<VersionDTO>> getVersionsByProjectId(@PathVariable String projectId);

    @GetMapping("/{versionId}")
    ResponseEntity<VersionDTO> getVersionById(@PathVariable String versionId);
    
    @GetMapping("/compare")
    ResponseEntity<VersionComparisonDTO> compareVersions(
            @RequestParam String projectId,
            @RequestParam(required = false) Integer beforeVersion,
            @RequestParam(required = false) Integer currentVersion);
            
    @PostMapping("/generate-ddl")
    ResponseEntity<DdlScriptResponse> generateDdlScript(@RequestBody DdlScriptRequest request);
    
    @PostMapping("/generate-single-ddl")
    ResponseEntity<DdlScriptResponse> generateSingleVersionDdl(@RequestBody SingleVersionDdlRequest request);
    
    @PostMapping("/generate-changelog-ddl")
    ResponseEntity<DdlScriptResponse> generateChangeLogDdl(@RequestBody ChangeLogDdlRequest request);
} 