package com.vissoft.vn.dbdocs.infrastructure.config;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class CorsConfig {

    @Value("${cors.allowed-origins:http://localhost:4200,http://localhost:3000}")
    private String allowedOrigins;

    @Bean
    public CorsFilter corsFilter() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        final CorsConfiguration config = new CorsConfiguration();
        
        log.info("Initializing CORS filter with allowed origins: {}", allowedOrigins);
        
        // Luôn sử dụng allowedOriginPatterns với "*" để tránh vấn đề CORS
        config.setAllowedOriginPatterns(Collections.singletonList("*"));
        config.setAllowCredentials(true);
        
        // Allow all headers
        config.setAllowedHeaders(Arrays.asList(
            "Origin", "Content-Type", "Accept", "Authorization", 
            "X-Requested-With", "Access-Control-Request-Method", 
            "Access-Control-Request-Headers"
        ));
        
        // Allow all methods
        config.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD"
        ));
        
        // Expose headers
        config.setExposedHeaders(Arrays.asList(
            "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials",
            "Authorization", "Content-Disposition"
        ));
        
        // Max age
        config.setMaxAge(3600L);
        
        // Apply to all paths
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
} 