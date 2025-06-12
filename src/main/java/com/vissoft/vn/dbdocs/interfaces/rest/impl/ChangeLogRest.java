package com.vissoft.vn.dbdocs.interfaces.rest.impl;

import com.vissoft.vn.dbdocs.application.dto.ChangeLogCreateRequest;
import com.vissoft.vn.dbdocs.application.dto.ChangeLogDTO;
import com.vissoft.vn.dbdocs.domain.service.ChangeLogService;
import com.vissoft.vn.dbdocs.interfaces.rest.ChangeLogOperator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChangeLogRest implements ChangeLogOperator {

    private final ChangeLogService changeLogService;

    @Override
    public ResponseEntity<ChangeLogDTO> createChangeLog(ChangeLogCreateRequest request) {
        return ResponseEntity.ok(changeLogService.createChangeLog(request));
    }

    @Override
    public ResponseEntity<List<ChangeLogDTO>> getChangeLogsByProjectId(String projectId) {
        return ResponseEntity.ok(changeLogService.getChangeLogsByProjectId(projectId));
    }

//    @Override
//    public ResponseEntity<List<ChangeLogDTO>> getChangeLogsByVersionId(String versionId) {
//        return ResponseEntity.ok(changeLogService.getChangeLogsByVersionId(versionId));
//    }

    @Override
    public ResponseEntity<ChangeLogDTO> getChangeLogById(String changeLogId) {
        return ResponseEntity.ok(changeLogService.getChangeLogById(changeLogId));
    }
} 