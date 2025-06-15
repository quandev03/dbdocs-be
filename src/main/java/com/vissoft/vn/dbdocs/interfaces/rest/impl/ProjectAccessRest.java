package com.vissoft.vn.dbdocs.interfaces.rest.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.vissoft.vn.dbdocs.application.dto.ProjectAccessDTO;
import com.vissoft.vn.dbdocs.application.dto.ProjectAccessRequest;
import com.vissoft.vn.dbdocs.application.dto.ProjectPermissionRequest;
import com.vissoft.vn.dbdocs.application.dto.ProjectVisibilityRequest;
import com.vissoft.vn.dbdocs.domain.service.ProjectAccessService;
import com.vissoft.vn.dbdocs.infrastructure.security.SecurityUtils;
import com.vissoft.vn.dbdocs.interfaces.rest.ProjectAccessOperator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ProjectAccessRest implements ProjectAccessOperator {
    
    private final ProjectAccessService projectAccessService;
    private final SecurityUtils securityUtils;
    
    @Override
    public ResponseEntity<ProjectAccessDTO> addUserToProject(ProjectAccessRequest request) {
        return ResponseEntity.ok(projectAccessService.addUserToProject(request));
    }
    
    @Override
    public ResponseEntity<Void> changeProjectVisibility(ProjectVisibilityRequest request) {
        projectAccessService.changeProjectVisibility(request);
        return ResponseEntity.ok().build();
    }
    
    @Override
    public ResponseEntity<Map<String, Integer>> checkUserAccess(String projectId) {
        String userId = securityUtils.getCurrentUserId();
        Integer permission = projectAccessService.checkUserAccess(projectId, userId);
        Map<String, Integer> result = new HashMap<>();
        result.put("permission", permission);
        return ResponseEntity.ok(result);
    }
    
    @Override
    public ResponseEntity<Map<String, Integer>> checkUserPermissionLevel(String projectId) {
        log.info("REST request to check user permission level for project: {}", projectId);
        
        String userId = securityUtils.getCurrentUserId();
        Integer permissionLevel = projectAccessService.checkUserPermissionLevel(projectId, userId);
        
        Map<String, Integer> result = new HashMap<>();
        result.put("permissionLevel", permissionLevel);
        
        // Add a description for better understanding
        Map<Integer, String> descriptions = new HashMap<>();
        descriptions.put(1, "Owner");
        descriptions.put(2, "View");
        descriptions.put(3, "Edit");
        descriptions.put(4, "Denied");
        
        result.put("code", permissionLevel);
        
        log.info("User {} has permission level {} ({}) for project {}", 
                userId, permissionLevel, descriptions.get(permissionLevel), projectId);
        
        return ResponseEntity.ok(result);
    }
    
    @Override
    public ResponseEntity<ProjectAccessDTO> changeUserPermission(ProjectPermissionRequest request) {
        return ResponseEntity.ok(projectAccessService.changeUserPermission(request));
    }
    
    @Override
    public ResponseEntity<Void> removeUserFromProject(String projectId, String identifier) {
        projectAccessService.removeUserFromProject(projectId, identifier);
        return ResponseEntity.ok().build();
    }
    
    @Override
    public ResponseEntity<List<ProjectAccessDTO>> getUsersWithAccessToProject(String projectId) {
        return ResponseEntity.ok(projectAccessService.getUsersWithAccessToProject(projectId));
    }
} 