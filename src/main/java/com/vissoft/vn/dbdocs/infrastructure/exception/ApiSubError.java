package com.vissoft.vn.dbdocs.infrastructure.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiSubError {
    private String field;
    private String message;
    private Object rejectedValue;
} 