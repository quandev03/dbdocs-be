package com.vissoft.vn.dbdocs.infrastructure.exception;

public enum ErrorCode {
    // Common errors
    INVALID_REQUEST,
    RESOURCE_NOT_FOUND,
    UNAUTHORIZED,
    FORBIDDEN,
    INTERNAL_SERVER_ERROR,
    
    // User errors
    USER_NOT_FOUND,
    USER_ALREADY_EXISTS,
    INVALID_CREDENTIALS,
    
    // Project errors
    PROJECT_NOT_FOUND,
    PROJECT_ALREADY_EXISTS,
    NOT_PROJECT_OWNER,
    PROJECT_ACCESS_DENIED,
    
    // ProjectAccess errors
    USER_ALREADY_HAS_ACCESS,
    USER_DOES_NOT_HAVE_ACCESS,
    INVALID_PERMISSION,
    INVALID_VISIBILITY_TYPE,
    PASSWORD_REQUIRED,
    
    // ChangeLog errors
    CHANGELOG_NOT_FOUND,
    INVALID_CHANGELOG_DATA,
    
    // Version errors
    VERSION_NOT_FOUND,
    INVALID_VERSION,

    ERROR_PARSING_DBML,
} 