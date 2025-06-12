package com.vissoft.vn.dbdocs.interfaces.rest;

import com.vissoft.vn.dbdocs.application.dto.ProjectCreateRequest;
import com.vissoft.vn.dbdocs.application.dto.ProjectDTO;
import com.vissoft.vn.dbdocs.application.dto.ProjectResponse;
import com.vissoft.vn.dbdocs.application.dto.ProjectUpdateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/projects")
public interface ProjectManagerOperator {

    @PostMapping
    ResponseEntity<ProjectDTO> createProject(@RequestBody ProjectCreateRequest request);

    @PutMapping("/{projectId}")
    ResponseEntity<ProjectDTO> updateProject(
            @PathVariable String projectId,
            @RequestBody ProjectUpdateRequest request
    );

    @GetMapping("/{projectId}")
    ResponseEntity<ProjectDTO> getProjectById(@PathVariable String projectId);

    @GetMapping
    ResponseEntity<List<ProjectDTO>> getAllProjects();

    @DeleteMapping("/{projectId}")
    ResponseEntity<Void> deleteProject(@PathVariable String projectId);
}
