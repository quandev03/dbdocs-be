package com.vissoft.vn.dbdocs.infrastructure.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;

@Getter
@Configuration
public class JwtConfig {
    
    @Value("${jwt.expiration:86400000}") // 24 hours in milliseconds
    private Long expiration;
    
    @Value("${jwt.secret:c2VjcmV0LWtleS1mb3ItZGJkb2NzLWFwcC1kZXZlbG9wbWVudC0yMDI1LTA2LTExLXZpc3NvZnQ=}")
    private String secret;
    
    @Bean
    public SecretKey secretKey() {
        // Sử dụng một khóa cố định thay vì tạo mới mỗi lần
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

} 