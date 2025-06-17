package com.vissoft.vn.dbdocs.infrastructure.constant;

/**
 * Global constants for the application
 */
public final class Constants {
    
    private Constants() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * SQL related constants
     */
    public static final class SQL {
        /**
         * Dialect types
         */
        public static final class Dialect {
            public static final int MYSQL = 1;
            public static final int MARIADB = 2;
            public static final int POSTGRESQL = 3;
            public static final int ORACLE = 4;
            public static final int SQL_SERVER = 5;
            
            public static final String MYSQL_NAME = "MySQL";
            public static final String MARIADB_NAME = "MariaDB";
            public static final String POSTGRESQL_NAME = "PostgreSQL";
            public static final String ORACLE_NAME = "Oracle";
            public static final String SQL_SERVER_NAME = "SQL Server";
            public static final String UNKNOWN_NAME = "Unknown";
        }
        
        /**
         * SQL Keywords
         */
        public static final class Keywords {
            public static final String CREATE_TABLE = "CREATE TABLE";
            public static final String CREATE_TABLE_IF_NOT_EXISTS = "CREATE TABLE IF NOT EXISTS";
            public static final String DROP_TABLE = "DROP TABLE";
            public static final String DROP_TABLE_IF_EXISTS = "DROP TABLE IF EXISTS";
            public static final String ALTER_TABLE = "ALTER TABLE";
            public static final String ADD_COLUMN = "ADD COLUMN";
            public static final String DROP_COLUMN = "DROP COLUMN";
            public static final String MODIFY_COLUMN = "MODIFY COLUMN";
            public static final String ALTER_COLUMN = "ALTER COLUMN";
            public static final String TYPE = "TYPE";
        }
        
        /**
         * SQL Data Types
         */
        public static final class DataTypes {
            // MySQL/MariaDB Data Types
            public static final String INT = "INT";
            public static final String VARCHAR = "VARCHAR";
            public static final String TEXT = "TEXT";
            public static final String DATETIME = "DATETIME";
            public static final String DECIMAL = "DECIMAL";
            
            // PostgreSQL Data Types
            public static final String INTEGER = "INTEGER";
            public static final String TIMESTAMP = "TIMESTAMP";
            
            // Oracle Data Types
            public static final String NUMBER = "NUMBER";
            public static final String VARCHAR2 = "VARCHAR2";
            public static final String CLOB = "CLOB";
        }
        
        /**
         * SQL Identifiers
         */
        public static final class Identifiers {
            public static final String MYSQL_IDENTIFIER_START = "`";
            public static final String MYSQL_IDENTIFIER_END = "`";
            
            public static final String POSTGRESQL_ORACLE_IDENTIFIER_START = "\"";
            public static final String POSTGRESQL_ORACLE_IDENTIFIER_END = "\"";
            
            public static final String SQL_SERVER_IDENTIFIER_START = "[";
            public static final String SQL_SERVER_IDENTIFIER_END = "]";
        }
        
        /**
         * SQL Comments and Formatting
         */
        public static final class Formatting {
            public static final String COMMENT_PREFIX = "-- ";
            public static final String TABLE_ADDED = "Table added: ";
            public static final String TABLE_REMOVED = "Table removed: ";
            public static final String TABLE_MODIFIED = "Table modified: ";
            public static final String NEW_LINE = "\n";
            public static final String COMMA = ",";
            public static final String SEMICOLON = ";";
            public static final String OPEN_PARENTHESIS = "(";
            public static final String CLOSE_PARENTHESIS = ")";
        }
    }
    
    /**
     * Application related constants
     */
    public static final class App {
        public static final String DEFAULT_ENCODING = "UTF-8";
        public static final String API_VERSION = "v1";
        public static final int DEFAULT_PAGE_SIZE = 20;
    }
    
    /**
     * Date and time related constants
     */
    public static final class DateTime {
        public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
        public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
        public static final String ISO_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    }
    
    /**
     * Validation related constants
     */
    public static final class Validation {
        public static final int MIN_PASSWORD_LENGTH = 8;
        public static final int MAX_USERNAME_LENGTH = 50;
        public static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    }

    public static final class Permission {
        public static final int OWNER = 1;
        public static final int VIEWER = 2;
        public static final int EDITOR = 3;
        public static final int DEN = 4;
    }
} 