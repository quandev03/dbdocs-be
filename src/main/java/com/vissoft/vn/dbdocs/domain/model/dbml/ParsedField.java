package com.vissoft.vn.dbdocs.domain.model.dbml;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParsedField {
    private String dataType;
    private Map<String, Object> attributes;
}