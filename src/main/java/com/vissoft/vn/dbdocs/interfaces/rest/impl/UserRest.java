package com.vissoft.vn.dbdocs.interfaces.rest.impl;

import com.vissoft.vn.dbdocs.application.dto.UserDTO;
import com.vissoft.vn.dbdocs.domain.service.UserService;
import com.vissoft.vn.dbdocs.interfaces.rest.UserOperator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserRest implements UserOperator {
    
    private final UserService userService;
    
    @Override
    public ResponseEntity<UserDTO> getCurrentUser() {
        log.info("REST request to get current user information");
        return ResponseEntity.ok(userService.getCurrentUser());
    }
} 