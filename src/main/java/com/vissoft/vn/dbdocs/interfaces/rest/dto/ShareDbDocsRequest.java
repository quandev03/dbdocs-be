package com.vissoft.vn.dbdocs.interfaces.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShareDbDocsRequest {
    private String passwordShare;
    private int shareType; // 1: public, 2: private, 3: password protected
}
