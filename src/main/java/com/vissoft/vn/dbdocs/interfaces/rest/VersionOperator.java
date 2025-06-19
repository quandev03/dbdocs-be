package com.vissoft.vn.dbdocs.interfaces.rest;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vissoft.vn.dbdocs.application.dto.*;
import com.vissoft.vn.dbdocs.interfaces.rest.dto.CompareCodeResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/api/v1/versions")
public interface VersionOperator {
    @Operation(
        summary = "Create a new version",
        description = "Creates a new version for the specified project.",
        tags = {"Version Management"},
        responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "Version created successfully",
                content = @io.swagger.v3.oas.annotations.media.Content(
                    mediaType = "application/json",
                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = VersionDTO.class)
                )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "Invalid request data"
            )
        }
    )
    @PostMapping
    ResponseEntity<VersionDTO> createVersion(@RequestBody VersionCreateRequest request);

    @GetMapping("/project/{projectId}")
    ResponseEntity<List<VersionDTO>> getVersionsByProjectId(@PathVariable String projectId);

    @GetMapping("/{versionId}")
    ResponseEntity<VersionDTO> getVersionById(@PathVariable String versionId);

    @GetMapping("/compare")
    ResponseEntity<CompareCodeResponse> compareVersions(
            @RequestParam String projectId,
            @RequestParam(required = false) Integer beforeVersion,
            @RequestParam(required = false) Integer currentVersion) throws JsonProcessingException;

    @PostMapping("/generate-ddl")
    ResponseEntity<DdlScriptResponse> generateDdlScript(@RequestBody DdlScriptRequest request);

    @PostMapping("/generate-single-ddl")
    ResponseEntity<DdlScriptResponse> generateSingleVersionDdl(@RequestBody SingleVersionDdlRequest request);

    @PostMapping("/generate-changelog-ddl")
    ResponseEntity<DdlScriptResponse> generateChangeLogDdl(@RequestBody ChangeLogDdlRequest request);
}