package com.vissoft.vn.dbdocs.interfaces.rest;

import java.util.List;
import java.util.Map;

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
    
    @PostMapping("/add-user")
    ResponseEntity<ProjectAccessDTO> addUserToProject(@RequestBody ProjectAccessRequest request);
    
    @PutMapping("/visibility")
    ResponseEntity<Void> changeProjectVisibility(@RequestBody ProjectVisibilityRequest request);
    
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
    @GetMapping("/permission-level/{projectId}")
    ResponseEntity<Map<String, Integer>> checkUserPermissionLevel(@PathVariable String projectId);
    
    @PutMapping("/permission")
    ResponseEntity<ProjectAccessDTO> changeUserPermission(@RequestBody ProjectPermissionRequest request);
    
    @DeleteMapping("/{projectId}/{identifier}")
    ResponseEntity<Void> removeUserFromProject(
            @PathVariable String projectId,
            @PathVariable String identifier
    );
    
    @GetMapping("/list/{projectId}")
    ResponseEntity<List<ProjectAccessDTO>> getUsersWithAccessToProject(@PathVariable String projectId);
} 