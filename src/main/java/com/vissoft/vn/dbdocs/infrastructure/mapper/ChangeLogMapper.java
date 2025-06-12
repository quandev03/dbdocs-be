package com.vissoft.vn.dbdocs.infrastructure.mapper;

import com.vissoft.vn.dbdocs.application.dto.ChangeLogCreateRequest;
import com.vissoft.vn.dbdocs.application.dto.ChangeLogDTO;
import com.vissoft.vn.dbdocs.domain.entity.ChangeLog;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ChangeLogMapper {
    ChangeLogDTO toDTO(ChangeLog entity);
    ChangeLog toEntity(ChangeLogDTO dto);
    ChangeLog createRequestToEntity(ChangeLogCreateRequest request);
} 