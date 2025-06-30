package com.vissoft.vn.dbdocs.interfaces.rest;

import com.vissoft.vn.dbdocs.application.dto.ProjectCreateRequest;
import com.vissoft.vn.dbdocs.application.dto.ProjectDTO;
import com.vissoft.vn.dbdocs.application.dto.ProjectResponse;
import com.vissoft.vn.dbdocs.application.dto.ProjectUpdateRequest;
import com.vissoft.vn.dbdocs.application.dto.LatestProjectVersionResponse;
import com.vissoft.vn.dbdocs.interfaces.rest.dto.InputPasswordShare;
import com.vissoft.vn.dbdocs.interfaces.rest.dto.ShareDbDocsDto;
import com.vissoft.vn.dbdocs.interfaces.rest.dto.ShareDbDocsRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/projects")
public interface ProjectManagerOperator {

    /**     * Creates a new project with the provided details.
     *
     * @param request the project creation request containing project details
     * @return ResponseEntity containing the created ProjectDTO
     */
    @Operation(
            summary = "Create a new project",
            description = "Creates a new project with the provided details.",
            tags = {"Project Management"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Project created successfully",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = ProjectDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request data"
                    )
            }
    )
    @PostMapping
    ResponseEntity<ProjectDTO> createProject(@RequestBody ProjectCreateRequest request);

    /**
     * Updates an existing project with the provided details.
     *
     * @param projectId the ID of the project to update
     * @param request   the project update request containing updated project details
     * @return ResponseEntity containing the updated ProjectDTO
     */
    @Operation(
            summary = "Update an existing project",
            description = "Updates the details of an existing project.",
            tags = {"Project Management"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Project updated successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProjectDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Project not found"
                    )
            }
    )
    @PutMapping("/{projectId}")
    ResponseEntity<ProjectDTO> updateProject(
            @PathVariable String projectId,
            @RequestBody ProjectUpdateRequest request
    );

    /**
     * Retrieves a project by its ID.
     *
     * @param projectId the ID of the project to retrieve
     * @return ResponseEntity containing the ProjectDTO if found, or 404 if not found
     */
    @Operation(
            summary = "Get project by ID",
            description = "Retrieves the project details by its ID.",
            tags = {"Project Management"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Project retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProjectDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Project not found"
                    )
            }
    )
    @GetMapping("/{projectId}")
    ResponseEntity<ProjectDTO> getProjectById(@PathVariable String projectId);

    /**
     * Retrieves a shared project by its ID using a password.
     *
     * @param projectId the ID of the shared project
     * @param inputPasswordShare the password for accessing the shared project
     * @return ResponseEntity containing the ProjectDTO if found, or 404 if not found
     */
    @Operation(
            summary = "Get shared project by ID",
            description = "Retrieves a shared project by its ID using a password.",
            tags = {"Project Management"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Shared project retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProjectDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Project not found"
                    )
            }
    )
    @PostMapping("shared/{projectId}")
    ResponseEntity<ProjectDTO> getProjectSharedById(@PathVariable String projectId, @RequestBody InputPasswordShare inputPasswordShare);

    /**
     * Retrieves all projects for the current user.
     *
     * @return ResponseEntity containing a list of ProjectDTOs
     */
    @Operation(
            summary = "Get all projects",
            description = "Retrieves a list of all projects.",
            tags = {"Project Management"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of projects retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProjectDTO.class)
                            )
                    )
            }
    )
    @GetMapping
    ResponseEntity<List<ProjectDTO>> getAllProjects();

    /**
     * Retrieves a list of projects shared with the current user.
     *
     * @return ResponseEntity containing a list of ProjectResponse objects
     */
    @Operation(
            summary = "Get shared projects",
            description = "Retrieves a list of projects shared with the current user.",
            tags = {"Project Management"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of shared projects retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProjectResponse.class)
                            )
                    )
            }
    )
    @GetMapping("/shared")
    ResponseEntity<List<ProjectResponse>> getSharedProjects();

    /**
     * Retrieves the latest version information of a project together with its changelog.
     *
     * @param projectId ID of the project
     * @return ResponseEntity containing project information and its latest version
     */
    @GetMapping("/{projectId}/lastest-version")
    ResponseEntity<LatestProjectVersionResponse> getLastestVersionByProjectId(@PathVariable String projectId);

    /**
     * Deletes a project by its ID.
     *
     * @param projectId the ID of the project to delete
     * @return ResponseEntity with no content if successful, or 404 if not found
     */
    @Operation(
            summary = "Delete a project",
            description = "Deletes a project by its ID.",
            tags = {"Project Management"},
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Project deleted successfully"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Project not found"
                    )
            }
    )
    @DeleteMapping("/{projectId}")
    ResponseEntity<Void> deleteProject(@PathVariable String projectId);

    /**
     * Shares a project with specified users.
     *
     * @param projectId the ID of the project to share
     * @param shareDbDocsRequest the request containing details of users to share the project with
     * @return ResponseEntity containing ShareDbDocsDto if successful, or 400 if invalid request data
     */
    @Operation(
            summary = "Share a project",
            description = "Shares a project with specified users.",
            tags = {"Project Management"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Project shared successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ShareDbDocsDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request data"
                    )
            }
    )
    @PostMapping("/sharing/{projectId}")
    ResponseEntity<ShareDbDocsDto> shareProject(
            @PathVariable String projectId,
            @RequestBody ShareDbDocsRequest shareDbDocsRequest
    );
}
