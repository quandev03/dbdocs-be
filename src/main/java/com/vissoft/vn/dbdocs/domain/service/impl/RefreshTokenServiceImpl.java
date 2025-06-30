package com.vissoft.vn.dbdocs.domain.service.impl;

import com.vissoft.vn.dbdocs.application.dto.AuthResponse;
import com.vissoft.vn.dbdocs.domain.repository.UserRepository;
import com.vissoft.vn.dbdocs.domain.service.RefreshTokenService;
import com.vissoft.vn.dbdocs.infrastructure.config.JwtConfig;
import com.vissoft.vn.dbdocs.infrastructure.exception.BaseException;
import com.vissoft.vn.dbdocs.infrastructure.exception.ErrorCode;
import com.vissoft.vn.dbdocs.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final JwtConfig jwtConfig;

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        log.info("Refreshing access token using JWT refresh token");
        
        try {
            // Validate refresh token JWT
            Claims claims = jwtTokenProvider.validateRefreshToken(refreshToken);
            String userId = claims.getSubject();
            String email = claims.get("email", String.class);
            
            log.debug("Refresh token validated for user: {}", userId);
            
            // Verify user still exists in database
            if (!userRepository.existsById(userId)) {
                log.error("User not found for refresh token: {}", userId);
                throw BaseException.of(ErrorCode.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
            }
            
            // Generate new access token
            String newAccessToken = jwtTokenProvider.generateToken(userId, email);
            
            log.info("Access token refreshed successfully for user: {}", userId);
            
            return AuthResponse.builder()
                    .accessToken(newAccessToken)     // NEW access token
                    .refreshToken(refreshToken)      // SAME refresh token (reuse)
                    .tokenType("Bearer")
                    .expiresIn(jwtConfig.getExpiration())
                    .build();
                    
        } catch (ExpiredJwtException e) {
            log.warn("Refresh token expired: {}", e.getMessage());
            throw BaseException.of(ErrorCode.REFRESH_TOKEN_EXPIRED, HttpStatus.FORBIDDEN);
        } catch (IllegalArgumentException e) {
            log.error("Invalid refresh token: {}", e.getMessage());
            throw BaseException.of(ErrorCode.REFRESH_TOKEN_NOT_FOUND, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Error refreshing token: {}", e.getMessage(), e);
            throw BaseException.of(ErrorCode.REFRESH_TOKEN_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }
    }
} 