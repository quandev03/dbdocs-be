package com.vissoft.vn.dbdocs.infrastructure.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {
    private HttpStatus status;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime timestamp;
    
    private String code;
    private String message;
    private String debugMessage;
    private Map<String, Object> params;
    
    @Builder.Default
    private List<ApiSubError> subErrors = new ArrayList<>();
    
    public static ApiError of(HttpStatus status, String code, String message) {
        return ApiError.builder()
                .status(status)
                .timestamp(LocalDateTime.now())
                .code(code)
                .message(message)
                .build();
    }
    
    public static ApiError of(HttpStatus status, String code, String message, String debugMessage) {
        return ApiError.builder()
                .status(status)
                .timestamp(LocalDateTime.now())
                .code(code)
                .message(message)
                .debugMessage(debugMessage)
                .build();
    }
    
    public static ApiError of(HttpStatus status, String code, String message, Map<String, Object> params) {
        return ApiError.builder()
                .status(status)
                .timestamp(LocalDateTime.now())
                .code(code)
                .message(message)
                .params(params)
                .build();
    }
}

