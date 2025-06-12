package com.vissoft.vn.dbdocs.domain.model;

import com.vissoft.vn.dbdocs.domain.model.dbml.DbmlModel;
import com.vissoft.vn.dbdocs.domain.model.dbml.TableModel;
import com.vissoft.vn.dbdocs.domain.model.dbml.ColumnModel;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;

/**
 * Parser for DBML (Database Markup Language) content
 * Converts DBML text into a structured DbmlModel object
 */
@Slf4j
public class DbmlParser {
    
    /**
     * Parse DBML content into a DbmlModel
     * 
     * @param dbmlContent the DBML content as a string
     * @return DbmlModel representing the database structure
     */
    public DbmlModel parse(String dbmlContent) {
        if (dbmlContent == null || dbmlContent.trim().isEmpty()) {
            return DbmlModel.builder().build();
        }
        
        log.info("Parsing DBML content of size: {}", dbmlContent.length());
        
        DbmlModel model = DbmlModel.builder().build();
        
        try {
            // Simple parsing implementation for now
            // In a real implementation, this would use ANTLR or a proper parser
            
            String[] lines = dbmlContent.split("\n");
            String currentTable = null;
            TableModel currentTableModel = null;
            
            for (String line : lines) {
                line = line.trim();
                
                // Skip empty lines and comments
                if (line.isEmpty() || line.startsWith("//")) {
                    continue;
                }
                
                // Table definition
                if (line.startsWith("Table") && line.contains("{")) {
                    // Extract table name: "Table users {" -> "users"
                    currentTable = line.substring(6, line.indexOf("{")).trim();
                    currentTableModel = TableModel.builder().name(currentTable).build();
                    model.getTables().add(currentTableModel);
                    continue;
                }
                
                // Field definition (if we're inside a table)
                if (currentTable != null && currentTableModel != null && !line.equals("}") && line.contains(" ")) {
                    // Extract field definition
                    String fieldName = line.split(" ")[0].trim();
                    String fieldType = line.substring(line.indexOf(" ")).trim();
                    
                    // Clean up type definition
                    if (fieldType.endsWith(",")) {
                        fieldType = fieldType.substring(0, fieldType.length() - 1);
                    }
                    
                    // Add column to current table
                    ColumnModel column = ColumnModel.builder()
                            .name(fieldName)
                            .dataType(fieldType)
                            .build();
                    
                    currentTableModel.getColumns().add(column);
                }
                
                // End of table
                if (line.equals("}")) {
                    currentTable = null;
                    currentTableModel = null;
                }
            }
            
            log.info("Parsed {} tables from DBML content", model.getTables().size());
            return model;
            
        } catch (Exception e) {
            log.error("Error parsing DBML content", e);
            return DbmlModel.builder().build();
        }
    }
} 