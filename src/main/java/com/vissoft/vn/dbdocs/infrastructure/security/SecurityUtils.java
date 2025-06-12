package com.vissoft.vn.dbdocs.infrastructure.security;

import com.vissoft.vn.dbdocs.infrastructure.exception.BaseException;
import com.vissoft.vn.dbdocs.infrastructure.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public static String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw BaseException.of(ErrorCode.UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }
        return authentication.getName();
    }

    public boolean isCurrentUser(String userId) {
        return getCurrentUserId().equals(userId);
    }

    public void validateCurrentUser(String userId) {
        if (!isCurrentUser(userId)) {
            throw BaseException.of(ErrorCode.FORBIDDEN, HttpStatus.FORBIDDEN);
        }
    }
} 