package com.vissoft.vn.dbdocs.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Model representing a database structure parsed from DBML content
 */
@Data
@NoArgsConstructor
public class DatabaseModel {
    
    private Map<String, Table> tables = new HashMap<>();
    
    /**
     * Add a new table to the database model
     * @param tableName name of the table
     */
    public void addTable(String tableName) {
        if (!tables.containsKey(tableName)) {
            tables.put(tableName, new Table(tableName));
        }
    }
    
    /**
     * Add a field to a specific table
     * @param tableName name of the table
     * @param fieldName name of the field
     * @param fieldType type of the field
     */
    public void addField(String tableName, String fieldName, String fieldType) {
        if (tables.containsKey(tableName)) {
            tables.get(tableName).addField(new Field(fieldName, fieldType));
        }
    }
    
    /**
     * Get all tables in the database model
     * @return list of all tables
     */
    public List<Table> getTables() {
        return new ArrayList<>(tables.values());
    }
    
    /**
     * Model representing a database table
     */
    @Data
    public static class Table {
        private String name;
        private List<Field> fields = new ArrayList<>();
        
        public Table(String name) {
            this.name = name;
        }
        
        public void addField(Field field) {
            fields.add(field);
        }
    }
    
    /**
     * Model representing a field in a table
     */
    @Data
    public static class Field {
        private String name;
        private String type;
        
        public Field(String name, String type) {
            this.name = name;
            this.type = type;
        }
    }
} 