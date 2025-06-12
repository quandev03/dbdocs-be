package com.vissoft.vn.dbdocs.domain.service;

import com.vissoft.vn.dbdocs.application.dto.VersionComparisonDTO;

public interface VersionComparisonService {
    
    /**
     * So sánh 2 phiên bản DBML dựa trên project ID và version code
     * 
     * @param projectId ID của project
     * @param beforeVersion Code version trước đó (nếu null sẽ sử dụng phiên bản trước)
     * @param currentVersion Code version hiện tại (nếu null sẽ sử dụng phiên bản mới nhất)
     * @return DTO chứa kết quả so sánh
     */
    VersionComparisonDTO compareVersions(String projectId, Integer beforeVersion, Integer currentVersion);
} 