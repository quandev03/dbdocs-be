package com.vissoft.vn.dbdocs.domain.service;

import java.util.List;

import com.vissoft.vn.dbdocs.application.dto.ChangeLogCreateRequest;
import com.vissoft.vn.dbdocs.application.dto.ChangeLogDTO;

public interface ChangeLogService {
    ChangeLogDTO createChangeLog(ChangeLogCreateRequest request);
    List<ChangeLogDTO> getChangeLogsByProjectId(String projectId);
    ChangeLogDTO getChangeLogById(String changeLogId);
    void updateChangeLogVersion(String changeLogId, int newVersion);
    ChangeLogDTO getLatestChangeLogByProjectId(String projectId);
} 