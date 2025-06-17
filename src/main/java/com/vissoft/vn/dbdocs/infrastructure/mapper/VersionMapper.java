package com.vissoft.vn.dbdocs.infrastructure.mapper;

import com.vissoft.vn.dbdocs.application.dto.ChangeLogDTO;
import com.vissoft.vn.dbdocs.application.dto.VersionCreateRequest;
import com.vissoft.vn.dbdocs.application.dto.VersionDTO;
import com.vissoft.vn.dbdocs.domain.entity.Users;
import com.vissoft.vn.dbdocs.domain.entity.Version;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    @Mapping(target = "creatorName", ignore = true)
    @Mapping(target = "creatorEmail", ignore = true)
    @Mapping(target = "creatorAvatarUrl", ignore = true)
    VersionDTO toDTO(Version version);
    
    @Mapping(target = "changeLog", source = "changeLog")
    @Mapping(target = "content", source = "changeLog", qualifiedByName = "extractContent")
    @Mapping(target = "projectId", source = "version.projectId")
    @Mapping(target = "changeLogId", source = "version.changeLogId")
    @Mapping(target = "createdDate", source = "version.createdDate")
    @Mapping(target = "createdBy", source = "version.createdBy")
    @Mapping(target = "creatorName", ignore = true)
    @Mapping(target = "creatorEmail", ignore = true)
    @Mapping(target = "creatorAvatarUrl", ignore = true)
    VersionDTO toDTOWithChangeLog(Version version, ChangeLogDTO changeLog);
    
    @Named("extractContent")
    default String extractContent(ChangeLogDTO changeLog) {
        return changeLog != null ? changeLog.getContent() : null;
    }
    
    /**
     * Ánh xạ Version sang DTO và bổ sung thông tin người tạo
     * @param version Entity Version
     * @param changeLog ChangeLog liên kết
     * @param creator Thông tin người tạo
     * @return VersionDTO với thông tin người tạo
     */
    default VersionDTO toDTOWithCreator(Version version, ChangeLogDTO changeLog, Users creator) {
        Logger log = LoggerFactory.getLogger(VersionMapper.class);
        VersionDTO dto = toDTOWithChangeLog(version, changeLog);
        
        if (creator != null) {
            log.info("Setting creator info for version {} - name: {}, email: {}, avatarUrl: {}", 
                    version.getId(), creator.getFullName(), creator.getEmail(), creator.getAvatarUrl());
            dto.setCreatorName(creator.getFullName());
            dto.setCreatorEmail(creator.getEmail());
            dto.setCreatorAvatarUrl(creator.getAvatarUrl());
        } else {
            log.warn("Creator is null for version: {}", version.getId());
        }
        
        return dto;
    }
} 