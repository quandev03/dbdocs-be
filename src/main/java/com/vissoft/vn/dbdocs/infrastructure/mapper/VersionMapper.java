package com.vissoft.vn.dbdocs.infrastructure.mapper;

import com.vissoft.vn.dbdocs.application.dto.ChangeLogDTO;
import com.vissoft.vn.dbdocs.application.dto.VersionCreateRequest;
import com.vissoft.vn.dbdocs.application.dto.VersionDTO;
import com.vissoft.vn.dbdocs.domain.entity.Version;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", 
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {ChangeLogMapper.class})
public interface VersionMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "codeVersion", ignore = true)
    @Mapping(target = "diffChange", ignore = true)
    Version createRequestToEntity(VersionCreateRequest request);
    
    @Mapping(target = "changeLog", ignore = true)
    @Mapping(target = "content", ignore = true)
    VersionDTO toDTO(Version version);
    
    @Mapping(target = "changeLog", source = "changeLog")
    @Mapping(target = "content", source = "changeLog", qualifiedByName = "extractContent")
    @Mapping(target = "projectId", source = "version.projectId")
    @Mapping(target = "changeLogId", source = "version.changeLogId")
    @Mapping(target = "createdDate", source = "version.createdDate")
    @Mapping(target = "createdBy", source = "version.createdBy")
    VersionDTO toDTOWithChangeLog(Version version, ChangeLogDTO changeLog);
    
    @Named("extractContent")
    default String extractContent(ChangeLogDTO changeLog) {
        return changeLog != null ? changeLog.getContent() : null;
    }
} 