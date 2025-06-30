package com.vissoft.vn.dbdocs.interfaces.rest.impl;

import com.vissoft.vn.dbdocs.application.dto.AuthResponse;
import com.vissoft.vn.dbdocs.application.dto.RefreshTokenRequest;
import com.vissoft.vn.dbdocs.domain.service.RefreshTokenService;
import com.vissoft.vn.dbdocs.interfaces.rest.AuthOperator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthRest implements AuthOperator {

    private final RefreshTokenService refreshTokenService;

    @Override
    public ResponseEntity<AuthResponse> refreshToken(RefreshTokenRequest request) {
        log.info("REST request to refresh access token");
        AuthResponse authResponse = refreshTokenService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(authResponse);
    }
} 