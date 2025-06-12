package com.vissoft.vn.dbdocs.infrastructure.mapper;

import com.vissoft.vn.dbdocs.application.dto.ProjectCreateRequest;
import com.vissoft.vn.dbdocs.application.dto.ProjectDTO;

import com.vissoft.vn.dbdocs.domain.entity.Project;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {

    ProjectDTO toDTO(Project request);
    
    Project toEntity(ProjectDTO dto);

    Project projectCreateRequestToEntity(ProjectCreateRequest request);
} 