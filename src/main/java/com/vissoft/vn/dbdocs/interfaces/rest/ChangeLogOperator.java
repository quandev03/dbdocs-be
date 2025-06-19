package com.vissoft.vn.dbdocs.interfaces.rest;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    /**
     * Create a new change log entry
     *
     * @param request The request containing change log details
     * @return ResponseEntity with the created ChangeLogDTO
     */
    @Operation(
        summary = "Create a new change log entry",
        description = "Creates a new change log entry for tracking changes in the project.",
        tags = {"Change Log Management"},
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Change log created successfully",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ChangeLogDTO.class)
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid request data"
            )
        }
    )
    @PostMapping
    ResponseEntity<ChangeLogDTO> createChangeLog(@RequestBody ChangeLogCreateRequest request);

    /**
     * Get all change logs for a specific project
     *
     * @param projectId The ID of the project
     * @return ResponseEntity with a list of ChangeLogDTOs
     */
    @Operation(
        summary = "Get change logs by project ID",
        description = "Retrieves all change logs associated with a specific project.",
        tags = {"Change Log Management"},
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Change logs retrieved successfully",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ChangeLogDTO.class)
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Project not found or no change logs available"
            )
        })
    @GetMapping("/project/{projectId}")
    ResponseEntity<List<ChangeLogDTO>> getChangeLogsByProjectId(@PathVariable String projectId);

    /**
     * Get a change log by its ID
     *
     * @param changeLogId The ID of the change log
     * @return ResponseEntity with the ChangeLogDTO if found, or 404 Not Found
     */
    @Operation(
        summary = "Get change log by ID",
        description = "Retrieves a specific change log entry by its ID.",
        tags = {"Change Log Management"},
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Change log retrieved successfully",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ChangeLogDTO.class)
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Change log not found"
            )
        })
    @GetMapping("/{changeLogId}")
    ResponseEntity<ChangeLogDTO> getChangeLogById(@PathVariable String changeLogId);
    
    /**
     * Get the latest changelog for a project
     * Returns 204 No Content if no changelog exists for the project
     * 
     * @param projectId The ID of the project
     * @return ResponseEntity with the latest changelog or 204 No Content
     */
    @Operation(
        summary = "Get latest change log by project ID",
        description = "Retrieves the latest change log entry for a specific project.",
        tags = {"Change Log Management"},
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Latest change log retrieved successfully",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ChangeLogDTO.class)
                )
            ),
            @ApiResponse(
                responseCode = "204",
                description = "No change logs available for this project"
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Project not found"
            )
        })
    @GetMapping("/latest/project/{projectId}")
    ResponseEntity<ChangeLogDTO> getLatestChangeLogByProjectId(@PathVariable String projectId);
} 