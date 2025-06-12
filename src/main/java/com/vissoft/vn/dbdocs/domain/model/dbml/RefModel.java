package com.vissoft.vn.dbdocs.domain.model.dbml;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefModel {
    private String name;
    
    private EndpointRef from;
    private String cardinality; // ">" or "<" or "-"
    private EndpointRef to;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EndpointRef {
        private String tableName;
        private String columnName;
    }
} 