package com.vissoft.vn.dbdocs.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.vissoft.vn.dbdocs.infrastructure.security.JwtAuthenticationFilter;
import com.vissoft.vn.dbdocs.infrastructure.security.OAuth2LoginSuccessHandler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CorsFilter corsFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("Configuring security filter chain");
        
        // Cho phép OPTIONS request để CORS preflight có thể hoạt động
        http
            .csrf(AbstractHttpConfigurer::disable)
            // Tắt cấu hình CORS mặc định, để sử dụng SimpleCorsFilter thay thế
            .cors(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Cho phép OPTIONS request
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // Cho phép test CORS endpoint
                .requestMatchers("/api/cors-test", "/api/v1/test").permitAll()
                // Public endpoints
                .requestMatchers(HttpMethod.POST, "/api/token/**").permitAll()
                .requestMatchers("/", "/error", "/webjars/**", "/oauth-test.html", "/api-test.html",
                               "/oauth2/redirect.html", "/oauth2/authorization/**").permitAll()
                // Swagger UI endpoints
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // Cho phép truy cập không xác thực vào endpoint lấy thông tin người dùng
                .requestMatchers("/api/v1/users/**").permitAll()
                // API endpoints require authentication
                .requestMatchers("/api/**").authenticated()
                // All other requests need to be authenticated
                .anyRequest().authenticated())
            .oauth2Login(oauth2 -> oauth2
                .successHandler(oAuth2LoginSuccessHandler))
            // Add filters in correct order
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
