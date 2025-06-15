package com.vissoft.vn.dbdocs.interfaces.rest;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/test")
@Slf4j
public class ApiTestController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> testApi() {
        log.info("API test endpoint called");
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "API is working!");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
} 