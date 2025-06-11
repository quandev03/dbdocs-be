package com.vissoft.vn.dbdocs.infrastructure.config;

import io.jsonwebtoken.Jwts;
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
    
    @Bean
    public SecretKey secretKey() {
        return Jwts.SIG.HS256.key().build();
    }

} 