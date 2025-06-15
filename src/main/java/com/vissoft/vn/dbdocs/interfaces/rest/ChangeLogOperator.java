package com.vissoft.vn.dbdocs.interfaces.rest;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.vissoft.vn.dbdocs.application.dto.ChangeLogCreateRequest;
import com.vissoft.vn.dbdocs.application.dto.ChangeLogDTO;

@RequestMapping("/api/v1/changelogs")
public interface ChangeLogOperator {

    @PostMapping
    ResponseEntity<ChangeLogDTO> createChangeLog(@RequestBody ChangeLogCreateRequest request);

    @GetMapping("/project/{projectId}")
    ResponseEntity<List<ChangeLogDTO>> getChangeLogsByProjectId(@PathVariable String projectId);

//    @GetMapping("/version/{versionId}")
//    ResponseEntity<List<ChangeLogDTO>> getChangeLogsByVersionId(@PathVariable String versionId);

    @GetMapping("/{changeLogId}")
    ResponseEntity<ChangeLogDTO> getChangeLogById(@PathVariable String changeLogId);
    
    /**
     * Get the latest changelog for a project
     * Returns 204 No Content if no changelog exists for the project
     * 
     * @param projectId The ID of the project
     * @return ResponseEntity with the latest changelog or 204 No Content
     */
    @GetMapping("/latest/project/{projectId}")
    ResponseEntity<ChangeLogDTO> getLatestChangeLogByProjectId(@PathVariable String projectId);
} 