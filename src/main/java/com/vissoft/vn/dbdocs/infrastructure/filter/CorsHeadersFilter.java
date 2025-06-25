package com.vissoft.vn.dbdocs.infrastructure.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Order(-1) // Đảm bảo filter này chạy đầu tiên, trước cả logging filter
@Slf4j
public class CorsHeadersFilter extends OncePerRequestFilter {

    @Value("${cors.allowed-origins:http://dbdocs.mmoall.com}")
    private String allowedOrigins;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String origin = request.getHeader("Origin");
        log.info("Request from origin: {}", origin);
        
        // Kiểm tra xem origin có trong danh sách allowed origins không
        if (origin != null) {
            if ("*".equals(allowedOrigins.trim())) {
                // Nếu cho phép tất cả origin
                response.setHeader("Access-Control-Allow-Origin", origin);
            } else {
                // Nếu chỉ cho phép một số origin cụ thể
                List<String> origins = Arrays.asList(allowedOrigins.split(","));
                if (origins.contains(origin) || origins.contains(origin.trim())) {
                    response.setHeader("Access-Control-Allow-Origin", origin);
                } else {
                    // Nếu không có trong danh sách, sử dụng origin đầu tiên
                    response.setHeader("Access-Control-Allow-Origin", origins.get(0).trim());
                    log.warn("Origin {} not in allowed list, using first allowed origin: {}", 
                            origin, origins.get(0).trim());
                }
            }
        } else {
            // Nếu không có origin, sử dụng wildcard
            response.setHeader("Access-Control-Allow-Origin", "*");
        }
        
        // Thêm các CORS headers khác
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD, PATCH");
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, X-Requested-With, Accept, Origin, Access-Control-Request-Method, Access-Control-Request-Headers");
        response.setHeader("Access-Control-Max-Age", "3600");
        
        // Nếu là OPTIONS request (preflight), trả về OK ngay lập tức
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        
        // Tiếp tục xử lý request
        filterChain.doFilter(request, response);
    }
} 