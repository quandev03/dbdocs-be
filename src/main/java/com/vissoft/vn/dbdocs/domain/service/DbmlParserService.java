package com.vissoft.vn.dbdocs.domain.service;

import com.vissoft.vn.dbdocs.domain.model.dbml.DbmlModel;

public interface DbmlParserService {
    /**
     * Parse DBML content into a DbmlModel
     * 
     * @param dbmlContent The DBML content to parse
     * @return A DbmlModel representing the parsed content
     */
    DbmlModel parseDbml(String dbmlContent);
} 