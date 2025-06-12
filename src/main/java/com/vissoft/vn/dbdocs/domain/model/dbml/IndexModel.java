package com.vissoft.vn.dbdocs.domain.model.dbml;

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
public class IndexModel {
    private String name;
    private boolean isUnique;
    private String type;
    private String note;
    
    @Builder.Default
    private List<IndexColumn> columns = new ArrayList<>();
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IndexColumn {
        private String name;
        private String option; // "asc" or "desc"
    }
} 