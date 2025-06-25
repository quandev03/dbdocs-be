package com.vissoft.vn.dbdocs.infrastructure.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SimpleCorsFilter implements Filter {

    private final List<String> allowedOrigins = Arrays.asList(
            "http://localhost:4200", 
            "http://localhost:3000", 
            "http://127.0.0.1:4200",
            "http://127.0.0.1:3000",
            "https://dbdocs.mmoall.com"
    );

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;
        
        // Lấy origin từ request
        String origin = request.getHeader("Origin");
        
        // Nếu origin nằm trong danh sách cho phép, thêm vào response header
        if (origin != null && allowedOrigins.contains(origin)) {
            response.setHeader("Access-Control-Allow-Origin", origin);
        } else if (origin != null) {
            // Nếu không có trong danh sách, sử dụng origin đầu tiên trong danh sách (cho dev)
            response.setHeader("Access-Control-Allow-Origin", allowedOrigins.get(0));
        }
        
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT, PATCH");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, Accept, X-Requested-With");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        
        // Xử lý yêu cầu preflight OPTIONS
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            chain.doFilter(req, res);
        }
    }
    
    @Override
    public void init(FilterConfig filterConfig) {
    }
    
    @Override
    public void destroy() {
    }
} 