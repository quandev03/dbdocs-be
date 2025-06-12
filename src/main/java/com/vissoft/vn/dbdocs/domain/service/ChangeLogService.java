package com.vissoft.vn.dbdocs.domain.service;

import com.vissoft.vn.dbdocs.application.dto.ChangeLogCreateRequest;
import com.vissoft.vn.dbdocs.application.dto.ChangeLogDTO;

import java.util.List;

public interface ChangeLogService {
    ChangeLogDTO createChangeLog(ChangeLogCreateRequest request);
    List<ChangeLogDTO> getChangeLogsByProjectId(String projectId);
    ChangeLogDTO getChangeLogById(String changeLogId);
    void updateChangeLogVersion(String changeLogId, int newVersion);
} 