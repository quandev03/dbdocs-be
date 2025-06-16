package com.vissoft.vn.dbdocs.interfaces.rest;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/cors-test")
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class CorsTestController {
    
    @GetMapping
    public String testCors() {
        log.info("CORS test endpoint called");
        return "CORS is working!";
    }
} 