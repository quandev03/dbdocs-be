package com.vissoft.vn.dbdocs.interfaces.rest.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.vissoft.vn.dbdocs.application.dto.UserDTO;
import com.vissoft.vn.dbdocs.domain.service.UserService;
import com.vissoft.vn.dbdocs.interfaces.rest.UserOperator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000", "http://127.0.0.1:4200"}, maxAge = 3600, allowCredentials = "true")
public class UserRest implements UserOperator {
    
    private final UserService userService;
    
    @Override
    public ResponseEntity<UserDTO> getCurrentUser() {
        log.info("REST request to get current user information");
        return ResponseEntity.ok(userService.getCurrentUser());
    }
    
    @Override
    public ResponseEntity<UserDTO> getUserById(@PathVariable String userId) {
        log.info("REST request to get user information by ID: {}", userId);
        return ResponseEntity.ok(userService.getUserById(userId));
    }
} 