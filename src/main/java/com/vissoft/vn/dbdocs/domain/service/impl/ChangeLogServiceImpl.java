package com.vissoft.vn.dbdocs.domain.service.impl;

import com.vissoft.vn.dbdocs.application.dto.ChangeLogCreateRequest;
import com.vissoft.vn.dbdocs.application.dto.ChangeLogDTO;
import com.vissoft.vn.dbdocs.domain.entity.ChangeLog;
import com.vissoft.vn.dbdocs.domain.entity.Version;
import com.vissoft.vn.dbdocs.domain.repository.ChangeLogRepository;
import com.vissoft.vn.dbdocs.domain.repository.VersionRepository;
import com.vissoft.vn.dbdocs.domain.service.ChangeLogService;
import com.vissoft.vn.dbdocs.infrastructure.exception.BaseException;
import com.vissoft.vn.dbdocs.infrastructure.exception.ErrorCode;
import com.vissoft.vn.dbdocs.infrastructure.mapper.ChangeLogMapper;
import com.vissoft.vn.dbdocs.infrastructure.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChangeLogServiceImpl implements ChangeLogService {

    private final ChangeLogRepository changeLogRepository;
    private final VersionRepository versionRepository;
    private final ChangeLogMapper changeLogMapper;
    private final SecurityUtils securityUtils;

    private String generateCodeChangeLog(String projectId) {
        log.info("Generating code change log for project: {}", projectId);
        
        // Get latest version
        Version latestVersion = versionRepository.findLatestVersionByProjectId(projectId)
                .orElse(null);
        
        int versionNumber = latestVersion != null ? latestVersion.getCodeVersion() : 0;
        String versionPrefix = versionNumber + ".";
        
        log.info("Latest version for project {}: {}", projectId, versionNumber);
        
        // Get latest changelog for this version
        ChangeLog latestChangeLog = changeLogRepository.findLatestChangeLogByVersionPrefix(projectId, versionPrefix)
                .orElse(null);
        
        int nextIndex = 1;
        if (latestChangeLog != null) {
            String[] parts = latestChangeLog.getCodeChangeLog().split("\\.");
            if (parts.length == 3) {
                nextIndex = Integer.parseInt(parts[1]) + 1;
            }
            log.info("Latest changelog found: {}, next index will be: {}", latestChangeLog.getCodeChangeLog(), nextIndex);
        } else {
            log.info("No existing changelog found for version prefix: {}, starting with index 1", versionPrefix);
        }
        
        String codeChangeLog = String.format("%d.%d.0", versionNumber, nextIndex);
        log.info("Generated code change log: {}", codeChangeLog);
        return codeChangeLog;
    }

    @Override
    @Transactional
    public ChangeLogDTO createChangeLog(ChangeLogCreateRequest request) {
        log.info("Creating new changelog for project: {}", request.getProjectId());
        
        ChangeLog changeLog = changeLogMapper.createRequestToEntity(request);
        changeLog.setCodeChangeLog(generateCodeChangeLog(request.getProjectId()));
        
        ChangeLog savedChangeLog = changeLogRepository.save(changeLog);
        log.info("Changelog created successfully with ID: {}, code: {}", 
                savedChangeLog.getId(), savedChangeLog.getCodeChangeLog());
        
        return changeLogMapper.toDTO(savedChangeLog);
    }

    @Override
    @Transactional
    public void updateChangeLogVersion(String changeLogId, int newVersion) {
        log.info("Updating changelog version - ID: {}, new version: {}", changeLogId, newVersion);
        
        try {
            ChangeLog changeLog = changeLogRepository.findById(changeLogId)
                    .orElseThrow(() -> {
                        log.error("Changelog not found with ID: {}", changeLogId);
                        return BaseException.of(ErrorCode.CHANGELOG_NOT_FOUND);
                    });
            
            String oldCodeChangeLog = changeLog.getCodeChangeLog();
            // Set new version with index 1
            String newCodeChangeLog = String.format("%d.1.0", newVersion);
            changeLog.setCodeChangeLog(newCodeChangeLog);
            
            changeLogRepository.save(changeLog);
            log.info("Changelog version updated successfully from {} to {}", oldCodeChangeLog, newCodeChangeLog);
        } catch (Exception e) {
            log.error("Error updating changelog version for ID: {}", changeLogId, e);
            throw e;
        }
    }

    @Override
    public List<ChangeLogDTO> getChangeLogsByProjectId(String projectId) {
        log.info("Fetching changelogs for project: {}", projectId);
        
        List<ChangeLog> changelogs = changeLogRepository.findByProjectIdOrderByCreatedDateDesc(projectId);
        log.info("Found {} changelogs for project: {}", changelogs.size(), projectId);
        
        return changelogs.stream()
                .map(changeLogMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ChangeLogDTO getChangeLogById(String changeLogId) {
        log.info("Fetching changelog by ID: {}", changeLogId);
        
        try {
            ChangeLog changeLog = changeLogRepository.findById(changeLogId)
                    .orElseThrow(() -> {
                        log.error("Changelog not found with ID: {}", changeLogId);
                        return BaseException.of(ErrorCode.CHANGELOG_NOT_FOUND);
                    });
            
            log.info("Changelog found: {}", changeLog.getCodeChangeLog());
            return changeLogMapper.toDTO(changeLog);
        } catch (Exception e) {
            log.error("Error fetching changelog with ID: {}", changeLogId, e);
            throw e;
        }
    }
} 