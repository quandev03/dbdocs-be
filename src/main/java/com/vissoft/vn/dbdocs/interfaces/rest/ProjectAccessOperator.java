package com.vissoft.vn.dbdocs.interfaces.rest;

import com.vissoft.vn.dbdocs.application.dto.ProjectAccessDTO;
import com.vissoft.vn.dbdocs.application.dto.ProjectAccessRequest;
import com.vissoft.vn.dbdocs.application.dto.ProjectPermissionRequest;
import com.vissoft.vn.dbdocs.application.dto.ProjectVisibilityRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/api/v1/project-access")
public interface ProjectAccessOperator {
    
    @PostMapping("/add-user")
    ResponseEntity<ProjectAccessDTO> addUserToProject(@RequestBody ProjectAccessRequest request);
    
    @PutMapping("/visibility")
    ResponseEntity<Void> changeProjectVisibility(@RequestBody ProjectVisibilityRequest request);
    
    @GetMapping("/check/{projectId}")
    ResponseEntity<Map<String, Integer>> checkUserAccess(@PathVariable String projectId);
    
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