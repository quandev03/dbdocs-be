package com.vissoft.vn.dbdocs.domain.model.dbml;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ColumnModel {
    private String name;
    private String dataType;
    private String typeParam;
    private boolean isPrimaryKey;
    private boolean isUnique;
    private boolean isNotNull;
    private boolean isAutoIncrement;
    private String defaultValue;
    private String note;
    private RefValue reference;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefValue {
        private String tableName;
        private String columnName;
        private String cardinality; // ">" or "<" or "-"
    }
} 