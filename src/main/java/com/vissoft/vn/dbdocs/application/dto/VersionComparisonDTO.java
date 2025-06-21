package com.vissoft.vn.dbdocs.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VersionComparisonDTO {
    private String projectId;
    private Integer fromVersion;
    private Integer toVersion;
    private String diffSummary;
    private String diffChanges;
    
    @Builder.Default
    private List<TableDiff> tableDiffs = new ArrayList<>();
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TableDiff {
        private String tableName;
        private DiffType diffType;
        
        @Builder.Default
        private List<ColumnDiff> columnDiffs = new ArrayList<>();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ColumnDiff {
        private String columnName;
        private DiffType diffType;

        private String beforeType;
        private String currentType;

        private Object oldValue;
        private Object newValue;

        // --- THÊM LẠI TRƯỜNG NÀY ---
        @Builder.Default // Để nó không bị null khi dùng builder
        private List<String> changedProperties = new ArrayList<>();
    }
    
    public enum DiffType {
        ADDED,
        REMOVED,
        MODIFIED
    }
} 