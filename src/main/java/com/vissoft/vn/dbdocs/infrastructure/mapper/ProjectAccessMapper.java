package com.vissoft.vn.dbdocs.infrastructure.mapper;

import com.vissoft.vn.dbdocs.application.dto.ProjectAccessDTO;
import com.vissoft.vn.dbdocs.domain.entity.ProjectAccess;
import com.vissoft.vn.dbdocs.domain.entity.Users;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ProjectAccessMapper {
    @Mapping(source = "projectAccessId", target = "id")
    @Mapping(source = "projectId", target = "projectId")
    @Mapping(source = "identifier", target = "identifier")
    @Mapping(source = "permission", target = "permission")
    @Mapping(target = "userEmail", ignore = true)
    @Mapping(target = "userName", ignore = true)
    @Mapping(target = "avatarUrl", ignore = true)
    ProjectAccessDTO toDTO(ProjectAccess projectAccess);
    
    @Named("toDTOWithUser")
    default ProjectAccessDTO toDTOWithUser(ProjectAccess projectAccess, Users user) {
        ProjectAccessDTO dto = toDTO(projectAccess);
        if (user != null) {
            dto.setUserEmail(user.getEmail());
            dto.setUserName(user.getFullName());
            dto.setAvatarUrl(user.getAvatarUrl());
        }
        return dto;
    }
    
    @Mapping(source = "id", target = "projectAccessId")
    @Mapping(source = "projectId", target = "projectId")
    @Mapping(source = "identifier", target = "identifier")
    @Mapping(source = "permission", target = "permission")
    ProjectAccess toEntity(ProjectAccessDTO dto);
} 