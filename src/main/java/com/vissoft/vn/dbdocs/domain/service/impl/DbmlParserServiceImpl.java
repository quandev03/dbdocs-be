package com.vissoft.vn.dbdocs.domain.service.impl;

import com.vissoft.vn.dbdocs.domain.model.dbml.*;
import com.vissoft.vn.dbdocs.domain.service.DbmlParserService;
import com.vissoft.vn.dbdocs.infrastructure.exception.BaseException;
import com.vissoft.vn.dbdocs.infrastructure.exception.ErrorCode;
import com.vissoft.vn.dbdocs.infrastructure.util.DataUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DbmlParserServiceImpl implements DbmlParserService {

    @Override
    public DbmlModel parseDbml(String dbmlContent) {
        log.info("Starting DBML parsing process");
        if (DataUtils.isNull(dbmlContent) || dbmlContent.trim().isEmpty()) {
            log.warn("DBML content is empty or null, returning empty model");
            return new DbmlModel();
        }
        
        try {
            log.debug("Initializing DBML model");
            DbmlModel model = new DbmlModel();
            
            // Analyze the DBML content line by line
            log.debug("Splitting content into lines for analysis");
            String[] lines = dbmlContent.split("\n");
            log.debug("Total lines to process: {}", lines.length);

            handleEnumDefinition(lines, model);
            
            log.info("DBML parsing completed successfully. Found {} tables, {} enums, {} refs", 
                    model.getTables().size(), 
                    model.getEnums().size(),
                    model.getRefs().size());
            
            return model;
        } catch (StringIndexOutOfBoundsException e) {
            log.error("String index error during DBML parsing. Possible malformed DBML syntax: {}", e.getMessage(), e);
            throw BaseException.of(ErrorCode.ERROR_PARSING_DBML);
        } catch (Exception e) {
            log.error("Unexpected error during DBML parsing: {}", e.getMessage(), e);
            throw BaseException.of(ErrorCode.ERROR_PARSING_DBML);
        }
    }

    private void handleEnumDefinition(String[] lines, DbmlModel model) {
        // Implement logic here to parse enum definitions
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            log.trace("Processing line {}: {}", i + 1, line);

            // Project
            if (line.startsWith("Project") && line.contains("{")) {
                log.debug("Found Project definition at line {}", i + 1);
                String projectName = line.substring(7, line.indexOf("{")).trim();
                if (projectName.startsWith("'") || projectName.startsWith("\"")) {
                    projectName = projectName.substring(1, projectName.length() - 1);
                }
                model.setProjectName(projectName);
                log.debug("Set project name: {}", projectName);
            }

            // Table
            if (line.startsWith("Table") && line.contains("{")) {
                log.debug("Found Table definition at line {}", i + 1);
                TableModel table = new TableModel();
                String tableName = line.substring(5, line.indexOf("{")).trim();
                if (tableName.contains("as")) {
                    String[] parts = tableName.split("as");
                    table.setName(parts[0].trim());
                    table.setAlias(parts[1].trim());
                    log.debug("Parsed table with name: {} and alias: {}", parts[0].trim(), parts[1].trim());
                } else {
                    table.setName(tableName);
                    log.debug("Parsed table with name: {}", tableName);
                }
                log.trace("Added table to model: {}", table.getName());
                model.getTables().add(table);
            }
        }
    }
} 