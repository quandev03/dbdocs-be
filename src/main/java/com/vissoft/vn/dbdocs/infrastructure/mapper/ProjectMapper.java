package com.vissoft.vn.dbdocs.infrastructure.mapper;

import com.vissoft.vn.dbdocs.application.dto.ProjectCreateRequest;
import com.vissoft.vn.dbdocs.application.dto.ProjectDTO;
import com.vissoft.vn.dbdocs.application.dto.ProjectResponse;
import com.vissoft.vn.dbdocs.domain.entity.Project;
import com.vissoft.vn.dbdocs.domain.entity.Users;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {

    ProjectDTO toDTO(Project request);
    
    Project toEntity(ProjectDTO dto);

    Project projectCreateRequestToEntity(ProjectCreateRequest request);
    
    ProjectResponse toResponse(Project project);
    
    @Named("toResponseWithOwner")
    default ProjectResponse toResponseWithOwner(Project project, Users owner) {
        ProjectResponse response = toResponse(project);
        if (owner != null) {
            response.setOwnerEmail(owner.getEmail());
            response.setOwnerAvatarUrl(owner.getAvatarUrl());
        }
        return response;
    }
} 