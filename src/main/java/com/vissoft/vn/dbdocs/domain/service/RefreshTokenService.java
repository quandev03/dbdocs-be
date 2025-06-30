package com.vissoft.vn.dbdocs.domain.service;

import com.vissoft.vn.dbdocs.application.dto.AuthResponse;

public interface RefreshTokenService {
    
    /**
     * Refresh access token using refresh token
     * @param refreshToken the refresh token string
     * @return AuthResponse with new access token
     */
    AuthResponse refreshToken(String refreshToken);
} 