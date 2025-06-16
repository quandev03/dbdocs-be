package com.vissoft.vn.dbdocs.domain.service.impl;

import com.vissoft.vn.dbdocs.domain.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.vissoft.vn.dbdocs.application.dto.UserDTO;
import com.vissoft.vn.dbdocs.domain.entity.Users;
import com.vissoft.vn.dbdocs.domain.repository.UserRepository;
import com.vissoft.vn.dbdocs.infrastructure.exception.BaseException;
import com.vissoft.vn.dbdocs.infrastructure.exception.ErrorCode;
import com.vissoft.vn.dbdocs.infrastructure.mapper.UserMapper;
import com.vissoft.vn.dbdocs.infrastructure.security.SecurityUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SecurityUtils securityUtils;
    
    @Override
    public UserDTO getCurrentUser() {
        String currentUserId = securityUtils.getCurrentUserId();
        log.info("Getting current user information - userId: {}", currentUserId);
        
        try {
            Users user = userRepository.findById(currentUserId)
                    .orElseThrow(() -> {
                        log.error("User not found - userId: {}", currentUserId);
                        return BaseException.of(ErrorCode.USER_NOT_FOUND);
                    });
            
            log.info("Found user: {} with email: {}", user.getUserId(), user.getEmail());
            return userMapper.toDTO(user);
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error getting current user", e);
            throw BaseException.of(ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Override
    public UserDTO getUserById(String userId) {
        log.info("Getting user information by ID - userId: {}", userId);
        
        try {
            Users user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.error("User not found - userId: {}", userId);
                        return BaseException.of(ErrorCode.USER_NOT_FOUND);
                    });
            
            log.info("Found user: {} with email: {}", user.getUserId(), user.getEmail());
            return userMapper.toDTO(user);
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error getting user by ID", e);
            throw BaseException.of(ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
} 