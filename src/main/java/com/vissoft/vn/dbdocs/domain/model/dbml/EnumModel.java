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
public class EnumModel {
    private String name;
    
    @Builder.Default
    private List<EnumValue> values = new ArrayList<>();
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EnumValue {
        private String name;
        private String note;
    }
} 