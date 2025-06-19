package com.vissoft.vn.dbdocs.domain.service;

import java.util.List;

import com.vissoft.vn.dbdocs.application.dto.ChangeLogCreateRequest;
import com.vissoft.vn.dbdocs.application.dto.ChangeLogDTO;

public interface ChangeLogService {

    /**
     * Create a new change log entry
     *
     * @param request The request containing change log details
     * @return The created ChangeLogDTO
     */
    ChangeLogDTO createChangeLog(ChangeLogCreateRequest request);

    /**
     * Retrieve all change logs for a specific project
     *
     * @param projectId The ID of the project
     * @return A list of ChangeLogDTOs for the specified project
     */
    List<ChangeLogDTO> getChangeLogsByProjectId(String projectId);

    /**
     * Retrieve a change log by its ID
     *
     * @param changeLogId The ID of the change log
     * @return The ChangeLogDTO if found, null otherwise
     */
    ChangeLogDTO getChangeLogById(String changeLogId);

    /**
     * Update the version of a change log
     *
     * @param changeLogId The ID of the change log to update
     * @param newVersion The new version numbers to set
     */
    void updateChangeLogVersion(String changeLogId, int newVersion);

    /**
     * Get the latest change log for a project
     *
     * @param projectId The ID of the project
     * @return The latest ChangeLogDTO for the specified project, or null if no change logs exist
     */
    ChangeLogDTO getLatestChangeLogByProjectId(String projectId);
} 