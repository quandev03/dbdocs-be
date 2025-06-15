package com.vissoft.vn.dbdocs.interfaces.rest.impl;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.vissoft.vn.dbdocs.application.dto.ChangeLogCreateRequest;
import com.vissoft.vn.dbdocs.application.dto.ChangeLogDTO;
import com.vissoft.vn.dbdocs.domain.service.ChangeLogService;
import com.vissoft.vn.dbdocs.interfaces.rest.ChangeLogOperator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChangeLogRest implements ChangeLogOperator {

    private final ChangeLogService changeLogService;

    @Override
    public ResponseEntity<ChangeLogDTO> createChangeLog(ChangeLogCreateRequest request) {
        return ResponseEntity.ok(changeLogService.createChangeLog(request));
    }

    @Override
    public ResponseEntity<List<ChangeLogDTO>> getChangeLogsByProjectId(String projectId) {
        log.info("REST request to get changelogs for project: {}", projectId);
        List<ChangeLogDTO> changelogs = changeLogService.getChangeLogsByProjectId(projectId);
        
        // Log the first few changelogs to check if they have avatar URLs
        if (!changelogs.isEmpty()) {
            int logCount = Math.min(3, changelogs.size());
            for (int i = 0; i < logCount; i++) {
                ChangeLogDTO changelog = changelogs.get(i);
                log.info("Changelog {} - ID: {}, creatorName: {}, creatorAvatarUrl: {}", 
                    i, changelog.getChangeLogId(), changelog.getCreatorName(), changelog.getCreatorAvatarUrl());
            }
        }
        
        return ResponseEntity.ok(changelogs);
    }

//    @Override
//    public ResponseEntity<List<ChangeLogDTO>> getChangeLogsByVersionId(String versionId) {
//        return ResponseEntity.ok(changeLogService.getChangeLogsByVersionId(versionId));
//    }

    @Override
    public ResponseEntity<ChangeLogDTO> getChangeLogById(String changeLogId) {
        return ResponseEntity.ok(changeLogService.getChangeLogById(changeLogId));
    }
    
    @Override
    public ResponseEntity<ChangeLogDTO> getLatestChangeLogByProjectId(String projectId) {
        log.info("REST request to get latest changelog for project: {}", projectId);
        ChangeLogDTO latestChangeLog = changeLogService.getLatestChangeLogByProjectId(projectId);
        
        if (latestChangeLog == null) {
            log.info("No changelog found for project: {}, returning 204 No Content", projectId);
            return ResponseEntity.noContent().build();
        }
        
        log.info("Latest changelog found - ID: {}, creatorName: {}, creatorAvatarUrl: {}", 
            latestChangeLog.getChangeLogId(), latestChangeLog.getCreatorName(), latestChangeLog.getCreatorAvatarUrl());
        
        return ResponseEntity.ok(latestChangeLog);
    }
} 