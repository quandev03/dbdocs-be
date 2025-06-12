package com.vissoft.vn.dbdocs.domain.service.impl;

import com.vissoft.vn.dbdocs.domain.model.dbml.*;
import com.vissoft.vn.dbdocs.domain.service.DbmlParserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DbmlParserServiceImpl implements DbmlParserService {

    @Override
    public DbmlModel parseDbml(String dbmlContent) {
        log.info("Starting DBML parsing process");
        log.debug("Input DBML content length: {} characters", dbmlContent != null ? dbmlContent.length() : 0);
        
        if (dbmlContent == null || dbmlContent.trim().isEmpty()) {
            log.warn("DBML content is empty or null, returning empty model");
            return new DbmlModel();
        }
        
        // Dùng temporary implementation cho đến khi ANTLR được thiết lập đúng
        // Đây là phiên bản đơn giản để xây dựng cấu trúc, sẽ được thay thế bằng ANTLR parser thực tế
        
        try {
            log.debug("Initializing DBML model");
            DbmlModel model = new DbmlModel();
            
            // Phân tích cơ bản dựa trên các từ khóa
            log.debug("Splitting content into lines for analysis");
            String[] lines = dbmlContent.split("\n");
            log.debug("Total lines to process: {}", lines.length);
            
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
                    
                    // TODO: Parse table columns and other details
                    log.trace("Added table to model: {}", table.getName());
                    model.getTables().add(table);
                }
                
                // TODO: Parse other DBML elements (Enum, Ref, etc.)
            }
            
            log.info("DBML parsing completed successfully. Found {} tables, {} enums, {} refs", 
                    model.getTables().size(), 
                    model.getEnums().size(),
                    model.getRefs().size());
            
            return model;
        } catch (StringIndexOutOfBoundsException e) {
            log.error("String index error during DBML parsing. Possible malformed DBML syntax: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to parse DBML content: Malformed syntax", e);
        } catch (Exception e) {
            log.error("Unexpected error during DBML parsing: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to parse DBML content: " + e.getMessage(), e);
        }
    }
} 