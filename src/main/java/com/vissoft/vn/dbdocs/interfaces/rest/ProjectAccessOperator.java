package com.vissoft.vn.dbdocs.interfaces.rest;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.vissoft.vn.dbdocs.application.dto.ProjectAccessDTO;
import com.vissoft.vn.dbdocs.application.dto.ProjectAccessRequest;
import com.vissoft.vn.dbdocs.application.dto.ProjectPermissionRequest;
import com.vissoft.vn.dbdocs.application.dto.ProjectVisibilityRequest;

@RequestMapping("/api/v1/project-access")
public interface ProjectAccessOperator {

    /**
     * Add a user to a project with specified access permissions
     * Returns the updated ProjectAccessDTO
     */
    @Operation(
        summary = "Add a user to a project",
        description = "Adds a user to a project with specified access permissions.",
        tags = {"Project Access Management"},
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "User added successfully",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProjectAccessDTO.class)
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid request data"
            )
        }
    )
    @PostMapping("/add-user")
    ResponseEntity<ProjectAccessDTO> addUserToProject(@RequestBody ProjectAccessRequest request);

    /**
     * Change the visibility of a project (public/private)
     * Returns 204 No Content if the visibility is changed successfully
     */
    @Operation(
        summary = "Change project visibility",
        description = "Changes the visibility of a project (public/private).",
        tags = {"Project Management"},
        responses = {
            @ApiResponse(
                responseCode = "204",
                description = "Project visibility changed successfully"
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid request data"
            )
        }
    )
    @PutMapping("/visibility")
    ResponseEntity<Void> changeProjectVisibility(@RequestBody ProjectVisibilityRequest request);

    /**
     * Check if the current user has access to a specific project
     * Returns:
     * 1 - Owner (project creator)
     * 2 - View permission (explicitly granted)
     * 3 - Edit permission (explicitly granted)
     * 4 - Denied (no explicit permission)
     */
    @Operation(
        summary = "Check user access to project",
        description = "Checks if the current user has access to a specific project.",
        tags = {"Project Access Management"},
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Access check successful",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Map.class)
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Project not found"
            )
        }
    )
    @GetMapping("/check/{projectId}")
    ResponseEntity<Map<String, Integer>> checkUserAccess(@PathVariable String projectId);
    
    /**
     * Check the current user's permission level for a specific project
     * This only checks explicit permissions, ignoring project visibility settings
     * Returns:
     * 1 - Owner (project creator)
     * 2 - View permission (explicitly granted)
     * 3 - Edit permission (explicitly granted)
     * 4 - Denied (no explicit permission)
     */
    @Operation(
        summary = "Check user permission level for project",
        description = "Checks the current user's permission level for a specific project.",
        tags = {"Project Access Management"},
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Permission level check successful",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Map.class)
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Project not found"
            )
        }
    )
    @GetMapping("/permission-level/{projectId}")
    ResponseEntity<Map<String, Integer>> checkUserPermissionLevel(@PathVariable String projectId);

    /**
     * Change the permission level of a user for a specific project
     * Returns the updated ProjectAccessDTO
     */
    @Operation(
        summary = "Change user permission for project",
        description = "Changes the permission level of a user for a specific project.",
        tags = {"Project Access Management"},
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "User permission changed successfully",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProjectAccessDTO.class)
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid request data"
            )
        })
    @PutMapping("/permission")
    ResponseEntity<ProjectAccessDTO> changeUserPermission(@RequestBody ProjectPermissionRequest request);

    /**
     * Remove a user from a project
     * This will remove the user's access to the project
     */
    @Operation(
        summary = "Remove user from project",
        description = "Removes a user from a project, revoking their access.",
        tags = {"Project Access Management"},
        responses = {
            @ApiResponse(
                responseCode = "204",
                description = "User removed successfully"
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Project or user not found"
            )
        }
    )
    @DeleteMapping("/{projectId}/{identifier}")
    ResponseEntity<Void> removeUserFromProject(
            @PathVariable String projectId,
            @PathVariable String identifier
    );

    /**
     * Get a list of users with access to a specific project
     * Returns a list of ProjectAccessDTO containing user access details
     */
    @Operation(
        summary = "Get users with access to project",
        description = "Retrieves a list of users who have access to a specific project.",
        tags = {"Project Access Management"},
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Users retrieved successfully",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = List.class)
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Project not found"
            )
        })
    @GetMapping("/list/{projectId}")
    ResponseEntity<List<ProjectAccessDTO>> getUsersWithAccessToProject(@PathVariable String projectId);
} 