package com.vissoft.vn.dbdocs.infrastructure.filter;

import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Order(0) // Đảm bảo filter này chạy trước các filter khác
@Slf4j
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // Ghi log request
        logRequest(request);
        
        // Tiếp tục xử lý request
        filterChain.doFilter(request, response);
        
        // Ghi log response
        logResponse(response);
    }
    
    private void logRequest(HttpServletRequest request) {
        if (log.isDebugEnabled()) {
            log.debug("=========================== Request ===========================");
            log.debug("URI: {}", request.getRequestURI());
            log.debug("Method: {}", request.getMethod());
            log.debug("Headers: {}", Collections.list(request.getHeaderNames())
                    .stream()
                    .map(headerName -> headerName + ": " + request.getHeader(headerName))
                    .collect(Collectors.joining(", ")));
            log.debug("==============================================================");
        }
    }
    
    private void logResponse(HttpServletResponse response) {
        if (log.isDebugEnabled()) {
            log.debug("=========================== Response ==========================");
            log.debug("Status: {}", response.getStatus());
            log.debug("Headers: {}", response.getHeaderNames()
                    .stream()
                    .map(headerName -> headerName + ": " + response.getHeader(headerName))
                    .collect(Collectors.joining(", ")));
            log.debug("==============================================================");
        }
    }
} 