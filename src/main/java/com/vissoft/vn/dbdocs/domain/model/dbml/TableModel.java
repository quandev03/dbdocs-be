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
public class TableModel {
    private String name;
    private String alias;
    private String note;
    
    @Builder.Default
    private List<ColumnModel> columns = new ArrayList<>();
    
    @Builder.Default
    private List<IndexModel> indexes = new ArrayList<>();
} 