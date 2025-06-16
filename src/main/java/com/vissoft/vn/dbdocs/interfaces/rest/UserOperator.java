package com.vissoft.vn.dbdocs.interfaces.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.vissoft.vn.dbdocs.application.dto.UserDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "User API", description = "API for user management")
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000", "http://127.0.0.1:4200"}, maxAge = 3600, allowCredentials = "true")
public interface UserOperator {
    
    @Operation(
        summary = "Get current user information",
        description = "API to get information about the current authenticated user",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "User information retrieved successfully",
                content = @Content(schema = @Schema(implementation = UserDTO.class))
            ),
            @ApiResponse(
                responseCode = "401",
                description = "Unauthorized - User not authenticated"
            ),
            @ApiResponse(
                responseCode = "404",
                description = "User not found"
            )
        }
    )
    @GetMapping("/me")
    ResponseEntity<UserDTO> getCurrentUser();
    
    @Operation(
        summary = "Get user information by ID",
        description = "API to get user information by user ID",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "User information retrieved successfully",
                content = @Content(schema = @Schema(implementation = UserDTO.class))
            ),
            @ApiResponse(
                responseCode = "404",
                description = "User not found"
            )
        }
    )
    @GetMapping("/{userId}")
    ResponseEntity<UserDTO> getUserById(@PathVariable String userId);
} 