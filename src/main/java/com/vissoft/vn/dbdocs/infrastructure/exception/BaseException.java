package com.vissoft.vn.dbdocs.infrastructure.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@Getter
public class BaseException extends RuntimeException {
    private final ErrorCode errorCode;
    private final HttpStatus status;
    private final Map<String, Object> params;

    public BaseException(ErrorCode errorCode) {
        this(errorCode, HttpStatus.BAD_REQUEST, null);
    }

    public BaseException(ErrorCode errorCode, HttpStatus status) {
        this(errorCode, status, null);
    }

    public BaseException(ErrorCode errorCode, Map<String, Object> params) {
        this(errorCode, HttpStatus.BAD_REQUEST, params);
    }

    public BaseException(ErrorCode errorCode, HttpStatus status, Map<String, Object> params) {
        super(errorCode.name());
        this.errorCode = errorCode;
        this.status = status;
        this.params = params != null ? params : new HashMap<>();
    }

    public static BaseException of(ErrorCode errorCode) {
        return new BaseException(errorCode);
    }

    public static BaseException of(ErrorCode errorCode, HttpStatus status) {
        return new BaseException(errorCode, status);
    }

    public static BaseException of(ErrorCode errorCode, Map<String, Object> params) {
        return new BaseException(errorCode, params);
    }

    public static BaseException of(ErrorCode errorCode, HttpStatus status, Map<String, Object> params) {
        return new BaseException(errorCode, status, params);
    }
} 