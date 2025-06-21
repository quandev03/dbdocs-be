package com.vissoft.vn.dbdocs.infrastructure.util;

import com.nimbusds.jwt.util.DateUtils;

/**
 * Lớp tiện ích chứa các hằng số từ khóa SQL, được nhóm theo chức năng.
 * Lớp này được thiết kế để không thể khởi tạo.
 *
 * Cách dùng: SqlKeywords.DML.SELECT, SqlKeywords.Clauses.WHERE, ...
 */
public final class SqlKeywords {

    /**
     * Ngăn chặn việc khởi tạo lớp tiện ích này.
     */
    private SqlKeywords() {
    }

    /**
     * Data Definition Language (DDL) - Ngôn ngữ định nghĩa dữ liệu
     * Các lệnh dùng để định nghĩa hoặc thay đổi cấu trúc của cơ sở dữ liệu.
     */
    public static final class DDL {
        public static final String CREATE_DATABASE = "CREATE DATABASE";
        public static final String CREATE_TABLE = "CREATE TABLE";
        public static final String CREATE_INDEX = "CREATE INDEX";
        public static final String CREATE_VIEW = "CREATE VIEW";

        public static final String ALTER_DATABASE = "ALTER DATABASE";
        public static final String ALTER_TABLE = "ALTER TABLE";
        public static final String ALTER_COLUMN = "ALTER COLUMN"; // Thường dùng với ALTER TABLE

        public static final String DROP_DATABASE = "DROP DATABASE";
        public static final String DROP_TABLE = "DROP TABLE";
        public static final String DROP_INDEX = "DROP INDEX";
        public static final String DROP_VIEW = "DROP VIEW";
        public static final String DROP_COLUMN = "DROP COLUMN"; // Thường dùng với ALTER TABLE

        public static final String TRUNCATE_TABLE = "TRUNCATE TABLE";
        public static final String RENAME_TABLE = "RENAME TABLE"; // Cú pháp có thể khác nhau
        public static final String ADD_COLUMN = "ADD COLUMN"; // Cú pháp có thể khác nhau
    }

    /**
     * Data Manipulation Language (DML) - Ngôn ngữ thao tác dữ liệu
     * Các lệnh dùng để truy vấn và thao tác trên dữ liệu.
     */
    public static final class DML {
        public static final String SELECT = "SELECT";
        public static final String INSERT_INTO = "INSERT INTO";
        public static final String UPDATE = "UPDATE";
        public static final String DELETE = "DELETE";
    }

    /**
     * Data Control Language (DCL) - Ngôn ngữ điều khiển dữ liệu
     * Các lệnh dùng để quản lý quyền truy cập.
     */
    public static final class DCL {
        public static final String GRANT = "GRANT";
        public static final String REVOKE = "REVOKE";
    }

    /**
     * Transaction Control Language (TCL) - Ngôn ngữ điều khiển giao dịch
     * Các lệnh dùng để quản lý các giao dịch trong cơ sở dữ liệu.
     */
    public static final class TCL {
        public static final String COMMIT = "COMMIT";
        public static final String ROLLBACK = "ROLLBACK";
        public static final String SAVEPOINT = "SAVEPOINT";
        public static final String SET_TRANSACTION = "SET TRANSACTION";
    }

    /**
     * Clauses - Các mệnh đề
     * Các thành phần cấu tạo nên một câu lệnh SQL hoàn chỉnh.
     */
    public static final class Clauses {
        public static final String FROM = "FROM";
        public static final String WHERE = "WHERE";
        public static final String GROUP_BY = "GROUP BY";
        public static final String HAVING = "HAVING";
        public static final String ORDER_BY = "ORDER BY";
        public static final String LIMIT = "LIMIT";   // MySQL, PostgreSQL
        public static final String TOP = "TOP";     // SQL Server
        public static final String FETCH_FIRST = "FETCH FIRST"; // Oracle, DB2
        public static final String VALUES = "VALUES";
        public static final String SET = "SET";
        public static final String AS = "AS";
        public static final String ON = "ON";
        public static final String JOIN = "JOIN";
        public static final String INNER_JOIN = "INNER JOIN";
        public static final String LEFT_JOIN = "LEFT JOIN";
        public static final String RIGHT_JOIN = "RIGHT JOIN";
        public static final String FULL_OUTER_JOIN = "FULL OUTER JOIN";
        public static final String UNION = "UNION";
        public static final String UNION_ALL = "UNION ALL";
    }

    /**
     * Operators - Các toán tử
     * Dùng trong các biểu thức điều kiện.
     */
    public static final class Operators {
        public static final String ALL = "ALL";
        public static final String AND = "AND";
        public static final String ANY = "ANY";
        public static final String BETWEEN = "BETWEEN";
        public static final String EXISTS = "EXISTS";
        public static final String IN = "IN";
        public static final String LIKE = "LIKE";
        public static final String NOT = "NOT";
        public static final String OR = "OR";
        public static final String IS_NULL = "IS NULL";
        public static final String IS_NOT_NULL = "IS NOT NULL";
        public static final String UNIQUE = "UNIQUE";
    }

    /**
     * Constraints - Các ràng buộc
     * Dùng để định nghĩa các quy tắc cho dữ liệu trong bảng.
     */
    public static final class Constraints {
        public static final String PRIMARY_KEY = "PRIMARY KEY";
        public static final String FOREIGN_KEY = "FOREIGN KEY";
        public static final String UNIQUE = "UNIQUE";
        public static final String NOT_NULL = "NOT NULL";
        public static final String CHECK = "CHECK";
        public static final String DEFAULT = "DEFAULT";
        public static final String INDEX = "INDEX";
        public static final String REFERENCES = "REFERENCES";
    }

    /**
     * Aggregate Functions - Các hàm tổng hợp
     * Các hàm thực hiện tính toán trên một tập các giá trị.
     */
    public static final class Functions {
        public static final String COUNT = "COUNT";
        public static final String SUM = "SUM";
        public static final String AVG = "AVG";
        public static final String MIN = "MIN";
        public static final String MAX = "MAX";
    }

    /**
     * Data Types - Các kiểu dữ liệu
     * Các kiểu dữ liệu phổ biến trong SQL.
     */
    public static final class DataTypes {
        // String types
        public static String varcharG(int length) {
            if(DataUtils.isNull(length)){
                return VARCHAR+"(255)";
            }
            return VARCHAR+"("+ length + ")";
        }
        public static String nVarcharG(int length) {
            if(DataUtils.isNull(length)){
                return NVARCHAR+"(255)";
            }
            return NVARCHAR+"("+ length + ")";
        }
        public static final String VARCHAR = "VARCHAR";
        public static final String NVARCHAR = "NVARCHAR";
        public static final String CHAR = "CHAR";
        public static final String TEXT = "TEXT";

        // Numeric types
        public static final String INT = "INT";
        public static final String INTEGER = "INTEGER";
        public static final String BIGINT = "BIGINT";
        public static final String SMALLINT = "SMALLINT";
        public static final String DECIMAL = "DECIMAL";
        public static final String NUMERIC = "NUMERIC";
        public static final String FLOAT = "FLOAT";
        public static final String REAL = "REAL";

        // Date and time types
        public static final String DATE = "DATE";
        public static final String TIME = "TIME";
        public static final String DATETIME = "DATETIME";
        public static final String TIMESTAMP = "TIMESTAMP";

        // Other types
        public static final String BOOLEAN = "BOOLEAN";
        public static final String BIT = "BIT";
        public static final String BLOB = "BLOB";
    }

    /**
     * Ordering - Sắp xếp
     */
    public static final class Ordering {
        public static final String ASC = "ASC";
        public static final String DESC = "DESC";
    }

    public static final class KeySqlConstant {
        public static final String PK_KEY = "PK";
        public static final String NOT_NULL = "NOT NULL";
    }

    public static final class ModifiersType {
        public static final String ADD = "ADD";
        public static final String MODIFY = "MODIFY";
        public static final String DROP = "DROP";
        public static final String RENAME = "RENAME";
        public static final String CHANGE = "CHANGE";
        public static final String ENABLE = "ENABLE";
        public static final String DISABLE = "DISABLE";
        public static final String COMMENT = "COMMENT";
        public static final String USE = "USE";
        public static final String ALTER = "ALTER";
        public static final String REMOVE = "REMOVED";
    }
}