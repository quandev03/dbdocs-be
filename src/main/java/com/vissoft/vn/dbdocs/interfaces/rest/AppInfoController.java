package com.vissoft.vn.dbdocs.interfaces.rest;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/app")
@Slf4j
@Tag(name = "Application Info API", description = "API for retrieving application information")
public class AppInfoController {
    
    @Value("${spring.application.version:1.0.0}")
    private String appVersion;
    
    @GetMapping("/version")
    @Operation(summary = "Get application version", description = "Returns the current version of the application")
    public ResponseEntity<Map<String, Object>> getAppVersion() {
        log.info("REST request to get application version");
        
        Map<String, Object> response = new HashMap<>();
        response.put("version", appVersion);
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
} 