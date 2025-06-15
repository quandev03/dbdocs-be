package com.vissoft.vn.dbdocs.interfaces.rest;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/cors-test")
@Slf4j
public class CorsTestController {

    @GetMapping
    @CrossOrigin(origins = "*", allowCredentials = "false")
    public Map<String, String> testCors() {
        log.info("CORS test endpoint called");
        Map<String, String> response = new HashMap<>();
        response.put("message", "CORS is working!");
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return response;
    }
} 