package com.vissoft.vn.dbdocs.infrastructure.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vissoft.vn.dbdocs.application.dto.ChangeLogCreateRequest;
import com.vissoft.vn.dbdocs.application.dto.ChangeLogDTO;
import com.vissoft.vn.dbdocs.domain.entity.ChangeLog;
import com.vissoft.vn.dbdocs.domain.entity.Users;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ChangeLogMapper {
    ChangeLogDTO toDTO(ChangeLog entity);
    ChangeLog toEntity(ChangeLogDTO dto);
    ChangeLog createRequestToEntity(ChangeLogCreateRequest request);
    
    /**
     * Ánh xạ ChangeLog sang DTO và bổ sung thông tin người dùng
     * @param entity Entity ChangeLog
     * @param creator Người tạo
     * @param modifier Người chỉnh sửa
     * @return ChangeLogDTO với thông tin người dùng
     */
    @Named("toDTOWithUserInfo")
    default ChangeLogDTO toDTOWithUserInfo(ChangeLog entity, Users creator, Users modifier) {
        Logger log = LoggerFactory.getLogger(ChangeLogMapper.class);
        ChangeLogDTO dto = toDTO(entity);
        
        if (creator != null) {
            log.info("Setting creator info - name: {}, avatarUrl: {}", creator.getFullName(), creator.getAvatarUrl());
            dto.setCreatorName(creator.getFullName());
            dto.setCreatorAvatarUrl(creator.getAvatarUrl());
        } else {
            log.warn("Creator is null for changelog: {}", entity.getId());
        }
        
        if (modifier != null) {
            log.info("Setting modifier info - name: {}, avatarUrl: {}", modifier.getFullName(), modifier.getAvatarUrl());
            dto.setModifierName(modifier.getFullName());
            dto.setModifierAvatarUrl(modifier.getAvatarUrl());
        } else {
            log.warn("Modifier is null for changelog: {}", entity.getId());
        }
        
        // Log the final DTO to verify the values
        log.info("Final DTO - creatorName: {}, creatorAvatarUrl: {}, modifierName: {}, modifierAvatarUrl: {}", 
                dto.getCreatorName(), dto.getCreatorAvatarUrl(), dto.getModifierName(), dto.getModifierAvatarUrl());
        
        return dto;
    }
} 