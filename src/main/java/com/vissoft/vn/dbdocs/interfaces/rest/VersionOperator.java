package com.vissoft.vn.dbdocs.interfaces.rest;

import com.vissoft.vn.dbdocs.application.dto.DdlScriptRequest;
import com.vissoft.vn.dbdocs.application.dto.DdlScriptResponse;
import com.vissoft.vn.dbdocs.application.dto.SingleVersionDdlRequest;
import com.vissoft.vn.dbdocs.application.dto.VersionComparisonDTO;
import com.vissoft.vn.dbdocs.application.dto.VersionCreateRequest;
import com.vissoft.vn.dbdocs.application.dto.VersionDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
} 