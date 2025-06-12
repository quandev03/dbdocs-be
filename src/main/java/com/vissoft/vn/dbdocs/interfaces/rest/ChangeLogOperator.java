package com.vissoft.vn.dbdocs.interfaces.rest;

import com.vissoft.vn.dbdocs.application.dto.ChangeLogCreateRequest;
import com.vissoft.vn.dbdocs.application.dto.ChangeLogDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
} 