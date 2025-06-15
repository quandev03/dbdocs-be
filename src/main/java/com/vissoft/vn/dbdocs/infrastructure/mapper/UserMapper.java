package com.vissoft.vn.dbdocs.infrastructure.mapper;

import com.vissoft.vn.dbdocs.application.dto.UserDTO;
import com.vissoft.vn.dbdocs.domain.entity.Users;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserDTO toDTO(Users user);
    Users toEntity(UserDTO dto);
} 