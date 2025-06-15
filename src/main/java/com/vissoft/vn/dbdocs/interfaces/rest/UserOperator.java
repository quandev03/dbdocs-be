package com.vissoft.vn.dbdocs.interfaces.rest;

import com.vissoft.vn.dbdocs.application.dto.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "User API", description = "API for user management")
@RequestMapping("/api/v1/users")
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
} 