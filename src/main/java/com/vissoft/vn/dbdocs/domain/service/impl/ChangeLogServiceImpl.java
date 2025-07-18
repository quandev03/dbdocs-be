package com.vissoft.vn.dbdocs.domain.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.vissoft.vn.dbdocs.infrastructure.constant.Constants;
import com.vissoft.vn.dbdocs.infrastructure.util.DataUtils;
import com.vissoft.vn.dbdocs.infrastructure.util.StringFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.vissoft.vn.dbdocs.application.dto.ChangeLogCreateRequest;
import com.vissoft.vn.dbdocs.application.dto.ChangeLogDTO;
import com.vissoft.vn.dbdocs.domain.entity.ChangeLog;
import com.vissoft.vn.dbdocs.domain.entity.Users;
import com.vissoft.vn.dbdocs.domain.entity.Version;
import com.vissoft.vn.dbdocs.domain.repository.ChangeLogRepository;
import com.vissoft.vn.dbdocs.domain.repository.UserRepository;
import com.vissoft.vn.dbdocs.domain.repository.VersionRepository;
import com.vissoft.vn.dbdocs.domain.service.ChangeLogService;
import com.vissoft.vn.dbdocs.domain.service.ProjectAccessService;
import com.vissoft.vn.dbdocs.infrastructure.exception.BaseException;
import com.vissoft.vn.dbdocs.infrastructure.exception.ErrorCode;
import com.vissoft.vn.dbdocs.infrastructure.mapper.ChangeLogMapper;
import com.vissoft.vn.dbdocs.infrastructure.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChangeLogServiceImpl implements ChangeLogService {

    private final ChangeLogRepository changeLogRepository;
    private final VersionRepository versionRepository;
    private final UserRepository userRepository;
    private final ChangeLogMapper changeLogMapper;
    private final ProjectAccessService projectAccessService;

    private String generateCodeChangeLog(String projectId) {
        log.info("Generating code change log for project: {}", projectId);
        
        // Get latest version
        Version latestVersion = versionRepository.findLatestVersionByProjectId(projectId)
                .orElse(null);
        int versionNumber = DataUtils.notNull(latestVersion) ? latestVersion.getCodeVersion() : 0;
        String versionPrefix = versionNumber + ".";
        log.info("Latest version for project {}: {}", projectId, versionNumber);
        
        // Get the latest changelog for this version
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
        String currentUserId = SecurityUtils.getCurrentUserId();
        Integer permission = projectAccessService.checkUserAccess(request.getProjectId(), currentUserId);
        if (DataUtils.isNull(permission) || Objects.equals(permission, Constants.Permission.VIEWER) ) {
            log.error(ErrorCode.PROJECT_ACCESS_DENIED.name());
            throw BaseException.of(ErrorCode.PROJECT_ACCESS_DENIED, HttpStatus.FORBIDDEN);
        }
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
                        log.error((ErrorCode.CHANGELOG_NOT_FOUND.name()));
                        return BaseException.of(ErrorCode.CHANGELOG_NOT_FOUND);
                    });
            String oldCodeChangeLog = changeLog.getCodeChangeLog();
            // Set the new version with index 1
            String newCodeChangeLog = String.format(StringFormat.CODE_CHANGE_LOG, newVersion);
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
        
        try {
            // Check user permission for project access
            String currentUserId = SecurityUtils.getCurrentUserId();
            Integer permission = projectAccessService.checkUserAccess(projectId, currentUserId);
            
            if (DataUtils.isNull(permission)) {
                log.error(ErrorCode.PROJECT_ACCESS_DENIED.name());
                throw BaseException.of(ErrorCode.PROJECT_ACCESS_DENIED, HttpStatus.FORBIDDEN);
            }
            
            List<ChangeLog> changelogs = changeLogRepository.findByProjectIdOrderByCreatedDateDesc(projectId);
            log.info("Found {} changelogs for project: {}", changelogs.size(), projectId);
            
            // Get creator and modifier user information
            Map<String, Users> userCache = new HashMap<>();
            
            for (ChangeLog changelog : changelogs) {
                if ( DataUtils.notNull(changelog.getCreatedBy()) && !userCache.containsKey(changelog.getCreatedBy())) {
                    userRepository.findById(changelog.getCreatedBy())
                        .ifPresent(user -> userCache.put(changelog.getCreatedBy(), user));
                }
                if (changelog.getModifiedBy() != null && !userCache.containsKey(changelog.getModifiedBy())) {
                    userRepository.findById(changelog.getModifiedBy())
                        .ifPresent(user -> userCache.put(changelog.getModifiedBy(), user));
                }
            }
            
            log.info("User cache populated with {} users", userCache.size());
            userCache.forEach((userId, user) -> log.info("Cached user - ID: {}, fullName: {}, avatarUrl: {}",
                userId, user.getFullName(), user.getAvatarUrl()));
            
            return changelogs.stream()
                    .map(changelog -> {
                        Users creator = userCache.get(changelog.getCreatedBy());
                        Users modifier = userCache.get(changelog.getModifiedBy());
                        return changeLogMapper.toDTOWithUserInfo(changelog, creator, modifier);
                    })
                    .toList();
        } catch (BaseException e) {
            log.error("Base exception occurred while fetching changelogs: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error fetching changelogs for project: {}", projectId, e);
            throw BaseException.of(ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
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

    @Override
    public ChangeLogDTO getLatestChangeLogByProjectId(String projectId) {
        log.info("Fetching latest changelog for project: {}", projectId);
        
        try {
            // Check user permission for project access
            String currentUserId = SecurityUtils.getCurrentUserId();
            Integer permission = projectAccessService.checkUserAccess(projectId, currentUserId);
            
            if (DataUtils.isNull(permission)) {
                log.error("User {} does not have permission to access project {}", currentUserId, projectId);
                throw BaseException.of(ErrorCode.PROJECT_ACCESS_DENIED, HttpStatus.FORBIDDEN);
            }
            
            // Get the latest changelog
            ChangeLog latestChangeLog = changeLogRepository.findLatestChangeLogByProjectId(projectId)
                    .orElse(null);
            
            // If no changelog found, return null
            if (DataUtils.isNull(latestChangeLog)) {
                log.info("No changelog found for project: {}", projectId);
                return null;
            }
            
            // Get creator and modifier user information
            Users creator = null;
            Users modifier = null;
            
            if (DataUtils.notNull(latestChangeLog.getCreatedBy())) {
                log.info("Finding creator user with ID: {}", latestChangeLog.getCreatedBy());
                creator = userRepository.findById(latestChangeLog.getCreatedBy()).orElse(null);
                if (DataUtils.notNull(creator)) {
                    log.info("Found creator user: {}, fullName: {}, avatarUrl: {}", 
                        creator.getUserId(), creator.getFullName(), creator.getAvatarUrl());
                } else {
                    log.warn("Creator user not found with ID: {}", latestChangeLog.getCreatedBy());
                }
            }
            
            if (DataUtils.notNull (latestChangeLog.getModifiedBy())) {
                log.info("Finding modifier user with ID: {}", latestChangeLog.getModifiedBy());
                modifier = userRepository.findById(latestChangeLog.getModifiedBy()).orElse(null);
                if (DataUtils.notNull(modifier)) {
                    log.info("Found modifier user: {}, fullName: {}, avatarUrl: {}", 
                        modifier.getUserId(), modifier.getFullName(), modifier.getAvatarUrl());
                } else {
                    log.warn("Modifier user not found with ID: {}", latestChangeLog.getModifiedBy());
                }
            }
            
            log.info("Latest changelog found: {} for project: {}", latestChangeLog.getCodeChangeLog(), projectId);
            return changeLogMapper.toDTOWithUserInfo(latestChangeLog, creator, modifier);
        } catch (BaseException e) {
            log.error("Base exception occurred while fetching latest changelog: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error fetching latest changelog for project: {}", projectId, e);
            throw BaseException.of(ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
} 