package com.vissoft.vn.dbdocs.domain.service;

import com.vissoft.vn.dbdocs.application.dto.VersionComparisonDTO;

public interface GeneraScriptDDLService {
    /**
     * Generates a DDL script based on the provided DBML content.
     *
     * @param dbmlContent The DBML content to generate the DDL script from.
     * @return The generated DDL script as a String.
     */
    String generateDDL(VersionComparisonDTO versionComparison, int sqlType);
}
