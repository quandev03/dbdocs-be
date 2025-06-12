package com.vissoft.vn.dbdocs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;

/**
 * Cấu hình tùy chỉnh cho Javers
 * Sử dụng Javers Core chỉ để phân tích sự khác biệt, không lưu vào cơ sở dữ liệu
 */
@Configuration
public class JaversConfiguration {
    
    @Bean
    public Javers javers() {
        return JaversBuilder.javers()
                .withPrettyPrint(true)
                .build();
    }
} 