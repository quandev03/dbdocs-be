package com.vissoft.vn.dbdocs.domain.model.dbml;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class TableModel {
    private String name;
    private String alias;
    private String note;
    
    @Builder.Default
    private List<ColumnModel> columns = new ArrayList<>();
    
    @Builder.Default
    private List<IndexModel> indexes = new ArrayList<>();
} 