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
public class DbmlModel {
    private String projectName;
    private String databaseType;
    private String note;
    
    @Builder.Default
    private List<TableModel> tables = new ArrayList<>();
    
    @Builder.Default
    private List<RefModel> refs = new ArrayList<>();
    
    @Builder.Default
    private List<EnumModel> enums = new ArrayList<>();
    
    @Builder.Default
    private List<TableGroupModel> tableGroups = new ArrayList<>();
} 