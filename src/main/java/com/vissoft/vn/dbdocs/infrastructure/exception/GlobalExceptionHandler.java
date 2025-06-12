package com.vissoft.vn.dbdocs.infrastructure.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<Object> handleBaseException(BaseException ex, WebRequest request) {
        String errorMessage = getMessage(ex.getErrorCode().name(), ex.getParams());
        
        ApiError apiError = ApiError.of(
                ex.getStatus(),
                ex.getErrorCode().name(),
                errorMessage,
                ex.getParams()
        );
        
        log.error("BaseException: {}", errorMessage, ex);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        ApiError apiError = ApiError.of(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorCode.INTERNAL_SERVER_ERROR.name(),
                getMessage(ErrorCode.INTERNAL_SERVER_ERROR.name()),
                ex.getLocalizedMessage()
        );
        
        log.error("Unhandled exception", ex);
        return buildResponseEntity(apiError);
    }

    // Method to get localized message
    private String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, code, LocaleContextHolder.getLocale());
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        
        List<ApiSubError> subErrors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = error instanceof FieldError ? ((FieldError) error).getField() : error.getObjectName();
            String errorMessage = error.getDefaultMessage();
            Object rejectedValue = error instanceof FieldError ? ((FieldError) error).getRejectedValue() : null;
            
            subErrors.add(ApiSubError.builder()
                    .field(fieldName)
                    .message(errorMessage)
                    .rejectedValue(rejectedValue)
                    .build());
        });
        
        ApiError apiError = ApiError.of(
                HttpStatus.BAD_REQUEST,
                ErrorCode.INVALID_REQUEST.name(),
                getMessage(ErrorCode.INVALID_REQUEST.name())
        );
        apiError.setSubErrors(subErrors);
        
        return buildResponseEntity(apiError);
    }

    protected ResponseEntity<Object> handleBindException(
            BindException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        
        return handleMethodArgumentNotValid(
                new MethodArgumentNotValidException(null, ex.getBindingResult()),
                headers,
                status,
                request
        );
    }

    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        
        ApiError apiError = ApiError.of(
                HttpStatus.BAD_REQUEST,
                ErrorCode.INVALID_REQUEST.name(),
                String.format("Parameter '%s' is missing", ex.getParameterName())
        );
        
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
        List<ApiSubError> subErrors = new ArrayList<>();
        ex.getConstraintViolations().forEach(violation -> {
            String fieldName = violation.getPropertyPath().toString();
            String errorMessage = violation.getMessage();
            Object rejectedValue = violation.getInvalidValue();
            
            subErrors.add(ApiSubError.builder()
                    .field(fieldName)
                    .message(errorMessage)
                    .rejectedValue(rejectedValue)
                    .build());
        });
        
        ApiError apiError = ApiError.of(
                HttpStatus.BAD_REQUEST,
                ErrorCode.INVALID_REQUEST.name(),
                getMessage(ErrorCode.INVALID_REQUEST.name())
        );
        apiError.setSubErrors(subErrors);
        
        return buildResponseEntity(apiError);
    }
} 