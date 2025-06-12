package com.vissoft.vn.dbdocs.infrastructure.mapper;

import com.vissoft.vn.dbdocs.application.dto.DdlScriptRequest;
import com.vissoft.vn.dbdocs.application.dto.DdlScriptResponse;
import com.vissoft.vn.dbdocs.application.dto.SingleVersionDdlRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface DdlScriptResponseMapper {
    
    @Mapping(target = "projectId", source = "request.projectId", qualifiedByName = "requestProjectId")
    @Mapping(target = "fromVersion", source = "request.fromVersion") 
    @Mapping(target = "toVersion", source = "request.toVersion")
    @Mapping(target = "dialect", source = "request.dialect")
    @Mapping(target = "ddlScript", source = "ddlScript")
    DdlScriptResponse toDdlScriptResponse(DdlScriptRequest request, String ddlScript);
    
    @Mapping(target = "projectId", source = "request.projectId", qualifiedByName = "singleRequestProjectId")
    @Mapping(target = "fromVersion", source = "request.versionNumber") 
    @Mapping(target = "toVersion", source = "request.versionNumber")
    @Mapping(target = "dialect", source = "request.dialect")
    @Mapping(target = "ddlScript", source = "ddlScript")
    DdlScriptResponse toSingleVersionDdlResponse(SingleVersionDdlRequest request, String ddlScript);
    
    @Named("requestProjectId")
    default String mapRequestProjectId(String projectId) {
        return projectId;
    }
    
    @Named("singleRequestProjectId")
    default String mapSingleRequestProjectId(String projectId) {
        return projectId;
    }
} 