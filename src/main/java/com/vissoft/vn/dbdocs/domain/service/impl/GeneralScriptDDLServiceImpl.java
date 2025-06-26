package com.vissoft.vn.dbdocs.domain.service.impl;

import com.vissoft.vn.dbdocs.application.dto.VersionComparisonDTO;
import com.vissoft.vn.dbdocs.domain.model.dbml.ParsedField;
import com.vissoft.vn.dbdocs.domain.service.GeneraScriptDDLService;
import com.vissoft.vn.dbdocs.infrastructure.constant.Constants;
import com.vissoft.vn.dbdocs.infrastructure.exception.BaseException;
import com.vissoft.vn.dbdocs.infrastructure.exception.ErrorCode;
import com.vissoft.vn.dbdocs.infrastructure.util.DataUtils;
import com.vissoft.vn.dbdocs.infrastructure.util.SqlKeywords;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class GeneralScriptDDLServiceImpl implements GeneraScriptDDLService {

    private static final String NEXT_LINE = "\n";
    private static final String SPACE = " ";
    private static final String COMMA = ", ";
    private static final String SEMICOLON = ";";
    private static final String OPEN_BRACKET = "(";
    private static final String CLOSE_BRACKET = ")";
    private static final String DOUBLE_QUOTE = "\"";
    private static final String SINGLE_QUOTE = "'";
    private static final String TAB = "\t";

    @Override
    public String generateDDL(VersionComparisonDTO versionComparison, int sqlType) {
        // Switch based on the SQL type
        switch (sqlType) {
            case Constants.SQL.Dialect.MYSQL, Constants.SQL.Dialect.MARIADB -> {
                return generateMysqlDDL(versionComparison);
            }
            case Constants.SQL.Dialect.POSTGRESQL -> {
                return generatePostgreSQLDDL(versionComparison);
            }
            case Constants.SQL.Dialect.ORACLE -> {
                return generateOracleDDL(versionComparison);
            }
            case Constants.SQL.Dialect.SQL_SERVER -> {
                return generateSqlServerDDL(versionComparison);
            }
            default -> throw new IllegalArgumentException("Unsupported SQL type: " + sqlType);
        }
    }

    private String generateOracleDDL(VersionComparisonDTO versionComparison) {
        // Implement Oracle DDL generation logic here

        StringBuilder ddlScript = new StringBuilder();

        log.info("Start generating Oracle DDL script");
        versionComparison.getTableDiffs().forEach(diff -> {
            // Check the type of difference and generate an appropriate DDL
            switch (diff.getDiffType()) {
                case ADDED:
                    // Generate DDL for table creation
                    String ddl = createTableDDLOracle(diff);
                    ddlScript.append(ddl).append(NEXT_LINE);
                    break;
                case REMOVED:
                    // Generate DDL for table drop
                    String dropDdl = removeTableDDLOracle(diff);
                    ddlScript.append(dropDdl).append(NEXT_LINE);
                    break;
                case MODIFIED:
                    // Generate DDL for table alteration
                    String modifyDdl = modifyTableDDLOracle(diff);
                    ddlScript.append(modifyDdl).append(NEXT_LINE);
                    break;
            }

        });
        return ddlScript.toString();
    }
    private String generateMysqlDDL(VersionComparisonDTO versionComparison) {
        // Implement MySQL DDL generation logic here

        StringBuilder ddlScript = new StringBuilder();

        log.info("Start generating MySQL DDL script");
        versionComparison.getTableDiffs().forEach(diff -> {
            // Check the type of difference and generate an appropriate DDL
            switch (diff.getDiffType()) {
                case ADDED:
                    // Generate DDL for table creation
                    String ddl = createTableDDLMySQL(diff);
                    ddlScript.append(ddl).append(NEXT_LINE);
                    break;
                case REMOVED:
                    // Generate DDL for table drop
                    String dropDdl = removeTableDDLOracle(diff);
                    ddlScript.append(dropDdl).append(NEXT_LINE);
                    break;
                case MODIFIED:
                    // Generate DDL for table alteration
                    String modifyDdl = modifyTableDDLMysql(diff);
                    ddlScript.append(modifyDdl).append(NEXT_LINE);
                    break;
            }

        });
        return ddlScript.toString();
    }


    // Implementations for Oracle DDL generation methods
    private String createTableDDLOracle(VersionComparisonDTO.TableDiff tableDiff) {
        StringBuilder ddl = new StringBuilder();
        String tableName = tableDiff.getTableName();
        
        ddl.append("-- ").append(SqlKeywords.DDL.CREATE_TABLE).append(SPACE).append(tableName).append(NEXT_LINE);
        ddl.append(SqlKeywords.DDL.CREATE_TABLE).append(SPACE).append(tableName).append(SPACE).append(OPEN_BRACKET).append(NEXT_LINE);
        
        for (VersionComparisonDTO.ColumnDiff columnDiff : tableDiff.getColumnDiffs()) {
            ParsedField parsedField = parse(columnDiff.getCurrentType());
            ddl.append(TAB).append(columnDiff.getColumnName()).append(SPACE).append(parsedField.getDataType());
            
            if(DataUtils.notNull(parsedField.getAttributes())) {
                for (Map.Entry<String, Object> entry : parsedField.getAttributes().entrySet()) {
                    String attributeStr = processAttribute(entry.getKey(), entry.getValue());
                    if (!attributeStr.isEmpty()) {
                        ddl.append(SPACE).append(attributeStr);
                    }
                }
            }
            ddl.append(COMMA).append(NEXT_LINE);
        }
        ddl.append(CLOSE_BRACKET).append(SEMICOLON).append(NEXT_LINE);
        return ddl.toString();
    }

    private String removeTableDDLOracle(VersionComparisonDTO.TableDiff tableDiff) {
        // Implement logic to create DDL for table removal
        StringBuilder ddl = new StringBuilder();
        ddl.append("-- ").append(SqlKeywords.DDL.DROP_TABLE).append(SPACE).append(tableDiff.getTableName()).append(NEXT_LINE);
        ddl.append(SqlKeywords.DDL.DROP_TABLE).append(SPACE).append(tableDiff.getTableName()).append(SEMICOLON).append(NEXT_LINE);
        return ddl.toString();
    }
    private String modifyTableDDLOracle(VersionComparisonDTO.TableDiff tableDiff) {
        StringBuilder ddl = new StringBuilder();
        
        for (VersionComparisonDTO.ColumnDiff columnDiff : tableDiff.getColumnDiffs()) {
            switch (columnDiff.getDiffType()) {
                case REMOVED:
                    ddl.append("-- ").append(SqlKeywords.DDL.ALTER_TABLE).append(SPACE).append(tableDiff.getTableName()).append(NEXT_LINE);
                    ddl.append(SqlKeywords.DDL.ALTER_TABLE).append(SPACE).append(tableDiff.getTableName())
                       .append(SPACE).append(SqlKeywords.DDL.DROP_COLUMN).append(SPACE)
                       .append(columnDiff.getColumnName()).append(SEMICOLON).append(NEXT_LINE);
                    break;
                case ADDED:
                    ParsedField parsedFieldAdd = parse(columnDiff.getCurrentType());
                    if(DataUtils.isNull(parsedFieldAdd)) throw BaseException.of(ErrorCode.PARSE_FIELD_ERROR);
                    ddl.append("-- ").append(SqlKeywords.DDL.ALTER_TABLE).append(SPACE).append(tableDiff.getTableName()).append(NEXT_LINE);
                    ddl.append(SqlKeywords.DDL.ALTER_TABLE).append(SPACE).append(tableDiff.getTableName()).append(SPACE)
                       .append(SqlKeywords.ModifiersType.ADD).append(SPACE)
                       .append(columnDiff.getColumnName()).append(SPACE).append(parsedFieldAdd.getDataType());
                    
                    if(DataUtils.notNull(parsedFieldAdd.getAttributes())) {
                        for (Map.Entry<String, Object> entry : parsedFieldAdd.getAttributes().entrySet()) {
                            String attributeStr = processAttribute(entry.getKey(), entry.getValue());
                            if (!attributeStr.isEmpty()) {
                                ddl.append(SPACE).append(attributeStr);
                            }
                        }
                    }
                    ddl.append(SEMICOLON).append(NEXT_LINE);
                    break;
                case MODIFIED:
                    ParsedField parsedFieldMod = parse(columnDiff.getCurrentType());
                    ddl.append("-- ").append(SqlKeywords.DDL.ALTER_TABLE).append(SPACE).append(tableDiff.getTableName()).append(NEXT_LINE);
                    ddl.append(SqlKeywords.DDL.ALTER_TABLE).append(SPACE).append(tableDiff.getTableName()).append(SPACE)
                       .append(SqlKeywords.ModifiersType.MODIFY).append(SPACE)
                       .append(columnDiff.getColumnName()).append(SPACE)
                       .append(parsedFieldMod.getDataType());
                    
                    if(DataUtils.notNull(parsedFieldMod.getAttributes())) {
                        for (Map.Entry<String, Object> entry : parsedFieldMod.getAttributes().entrySet()) {
                            String attributeStr = processAttribute(entry.getKey(), entry.getValue());
                            if (!attributeStr.isEmpty()) {
                                ddl.append(SPACE).append(attributeStr);
                            }
                        }
                    }
                    ddl.append(SEMICOLON).append(NEXT_LINE);
                    break;
            }
        }
        return ddl.toString();
    }


    // General DDL MySQL generation methods
    private String createTableDDLMySQL(VersionComparisonDTO.TableDiff tableDiff) {
        String tableName = tableDiff.getTableName();

        StringBuilder ddl = new StringBuilder();
        ddl.append("-- ").append(SqlKeywords.DDL.CREATE_TABLE).append(SPACE).append(tableName).append(NEXT_LINE);
        ddl.append(SqlKeywords.DDL.CREATE_TABLE)
                .append(SPACE).append(tableName)
                .append(SPACE).append(OPEN_BRACKET)
                .append(NEXT_LINE);

        for (VersionComparisonDTO.ColumnDiff columnDiff : tableDiff.getColumnDiffs()) {
            ParsedField parsedField = parse(columnDiff.getCurrentType());

            // Thêm tên cột và kiểu dữ liệu
            ddl.append(TAB).append(columnDiff.getColumnName()).append(SPACE).append(parsedField.getDataType());

            // Xử lý các thuộc tính (attributes)
            if (DataUtils.notNull(parsedField.getAttributes())) {
                for (Map.Entry<String, Object> attribute : parsedField.getAttributes().entrySet()) {
                    String attributeStr = processAttribute(attribute.getKey(), attribute.getValue());
                    if (!attributeStr.isEmpty()) {
                        ddl.append(SPACE).append(attributeStr);
                    }
                }
            }
            ddl.append(COMMA).append(NEXT_LINE);
        }
        ddl.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;").append(NEXT_LINE);
        return ddl.toString();
    }

    private String modifyTableDDLMysql(VersionComparisonDTO.TableDiff tableDiff) {
        // Implement logic to create DDL for table modification
        StringBuilder ddl = new StringBuilder();

        // generate DDL for each column diff separately
        StringBuilder ddlModifyTypeAdd = new StringBuilder();
        StringBuilder ddlModifyTypeRemove = new StringBuilder();
        StringBuilder ddlModifyTypeModify = new StringBuilder();
        int countRemove = 0;
        int countModify = 0;
        int countAdd = 0;

        for (VersionComparisonDTO.ColumnDiff columnDiff : tableDiff.getColumnDiffs()) {
            switch (columnDiff.getDiffType()) {
                case REMOVED:
                    countRemove++;
                    ddlModifyTypeRemove.append("-- ").append(SqlKeywords.DDL.ALTER_TABLE).append(SPACE).append(tableDiff.getTableName()).append(NEXT_LINE);
                    ddlModifyTypeRemove.append(SqlKeywords.DDL.ALTER_TABLE)
                            .append(SPACE)
                            .append(tableDiff.getTableName())
                            .append(SPACE)
                            .append(SqlKeywords.DDL.DROP_COLUMN)
                            .append(SPACE)
                            .append(columnDiff.getColumnName())
                            .append(SEMICOLON)
                            .append(NEXT_LINE);
                    break;
                case ADDED:
                    countAdd++;
                    ParsedField parsedFieldAdd = parse(columnDiff.getCurrentType());
                    if(DataUtils.isNull(parsedFieldAdd)) throw BaseException.of(ErrorCode.PARSE_FIELD_ERROR);
                    ddlModifyTypeAdd.append("-- ").append(SqlKeywords.DDL.ALTER_TABLE).append(SPACE).append(tableDiff.getTableName()).append(NEXT_LINE);
                    ddlModifyTypeAdd.append(SqlKeywords.DDL.ALTER_TABLE).append(SPACE).append(tableDiff.getTableName()).append(SPACE)
                            .append(SqlKeywords.DDL.ADD_COLUMN).append(SPACE)
                            .append(columnDiff.getColumnName()).append(SPACE)
                            .append(parsedFieldAdd.getDataType());
                    
                    if(DataUtils.notNull(parsedFieldAdd.getAttributes())) {
                        for (Map.Entry<String, Object> entry : parsedFieldAdd.getAttributes().entrySet()) {
                            String attributeStr = processAttribute(entry.getKey(), entry.getValue());
                            if (!attributeStr.isEmpty()) {
                                ddlModifyTypeAdd.append(SPACE).append(attributeStr);
                            }
                        }
                    }
                    ddlModifyTypeAdd.append(SEMICOLON).append(NEXT_LINE);
                    break;
                case MODIFIED:
                    countModify++;
                    ParsedField parsedFieldMod = parse(columnDiff.getCurrentType());
                    ddlModifyTypeModify.append("-- ").append(SqlKeywords.DDL.ALTER_TABLE).append(SPACE).append(tableDiff.getTableName()).append(NEXT_LINE);
                    ddlModifyTypeModify.append(SqlKeywords.DDL.ALTER_TABLE).append(SPACE).append(tableDiff.getTableName()).append(SPACE)
                            .append("MODIFY COLUMN").append(SPACE)
                            .append(columnDiff.getColumnName()).append(SPACE)
                            .append(parsedFieldMod.getDataType());
                    
                    if(DataUtils.notNull(parsedFieldMod.getAttributes())) {
                        for (Map.Entry<String, Object> entry : parsedFieldMod.getAttributes().entrySet()) {
                            String attributeStr = processAttribute(entry.getKey(), entry.getValue());
                            if (!attributeStr.isEmpty()) {
                                ddlModifyTypeModify.append(SPACE).append(attributeStr);
                            }
                        }
                    }
                    ddlModifyTypeModify.append(SEMICOLON).append(NEXT_LINE);
                    break;
            }
        }

        if(countRemove > 0) {
            ddl.append(ddlModifyTypeRemove).append(NEXT_LINE);
        }
        if (countAdd > 0) {
            ddl.append(ddlModifyTypeAdd).append(NEXT_LINE);
        }
        if(countModify > 0) {
            ddl.append(ddlModifyTypeModify).append(NEXT_LINE);
        }
        return ddl.toString();
    }

    // PostgreSQL DDL generation methods
    private String generatePostgreSQLDDL(VersionComparisonDTO versionComparison) {
        StringBuilder ddlScript = new StringBuilder();

        log.info("Start generating PostgreSQL DDL script");
        versionComparison.getTableDiffs().forEach(diff -> {
            switch (diff.getDiffType()) {
                case ADDED:
                    String ddl = createTableDDLPostgreSQL(diff);
                    ddlScript.append(ddl).append(NEXT_LINE);
                    break;
                case REMOVED:
                    String dropDdl = removeTableDDLPostgreSQL(diff);
                    ddlScript.append(dropDdl).append(NEXT_LINE);
                    break;
                case MODIFIED:
                    String modifyDdl = modifyTableDDLPostgreSQL(diff);
                    ddlScript.append(modifyDdl).append(NEXT_LINE);
                    break;
            }
        });
        return ddlScript.toString();
    }

    private String createTableDDLPostgreSQL(VersionComparisonDTO.TableDiff tableDiff) {
        String tableName = tableDiff.getTableName();
        StringBuilder ddl = new StringBuilder();
        
        ddl.append("-- ").append(SqlKeywords.DDL.CREATE_TABLE).append(SPACE).append(tableName).append(NEXT_LINE);
        ddl.append(SqlKeywords.DDL.CREATE_TABLE).append(SPACE).append(tableName).append(SPACE).append(OPEN_BRACKET).append(NEXT_LINE);

        for (VersionComparisonDTO.ColumnDiff columnDiff : tableDiff.getColumnDiffs()) {
            ParsedField parsedField = parse(columnDiff.getCurrentType());
            ddl.append(TAB).append(columnDiff.getColumnName()).append(SPACE).append(parsedField.getDataType());
            
            if (DataUtils.notNull(parsedField.getAttributes())) {
                for (Map.Entry<String, Object> attribute : parsedField.getAttributes().entrySet()) {
                    String attributeStr = processAttribute(attribute.getKey(), attribute.getValue());
                    if (!attributeStr.isEmpty()) {
                        ddl.append(SPACE).append(attributeStr);
                    }
                }
            }
            ddl.append(COMMA).append(NEXT_LINE);
        }
        ddl.append(CLOSE_BRACKET).append(SEMICOLON).append(NEXT_LINE);
        return ddl.toString();
    }

    private String removeTableDDLPostgreSQL(VersionComparisonDTO.TableDiff tableDiff) {
        StringBuilder ddl = new StringBuilder();
        ddl.append("-- ").append(SqlKeywords.DDL.DROP_TABLE).append(SPACE).append(tableDiff.getTableName()).append(NEXT_LINE);
        ddl.append(SqlKeywords.DDL.DROP_TABLE).append(SPACE).append(tableDiff.getTableName()).append(SEMICOLON).append(NEXT_LINE);
        return ddl.toString();
    }

    private String modifyTableDDLPostgreSQL(VersionComparisonDTO.TableDiff tableDiff) {
        StringBuilder ddl = new StringBuilder();
        
        for (VersionComparisonDTO.ColumnDiff columnDiff : tableDiff.getColumnDiffs()) {
            switch (columnDiff.getDiffType()) {
                case REMOVED:
                    ddl.append("-- ").append(SqlKeywords.DDL.ALTER_TABLE).append(SPACE).append(tableDiff.getTableName()).append(NEXT_LINE);
                    ddl.append(SqlKeywords.DDL.ALTER_TABLE).append(SPACE).append(tableDiff.getTableName())
                       .append(SPACE).append(SqlKeywords.DDL.DROP_COLUMN).append(SPACE)
                       .append(columnDiff.getColumnName()).append(SEMICOLON).append(NEXT_LINE);
                    break;
                case ADDED:
                    ParsedField parsedFieldAdd = parse(columnDiff.getCurrentType());
                    if(DataUtils.isNull(parsedFieldAdd)) throw BaseException.of(ErrorCode.PARSE_FIELD_ERROR);
                    ddl.append("-- ").append(SqlKeywords.DDL.ALTER_TABLE).append(SPACE).append(tableDiff.getTableName()).append(NEXT_LINE);
                    ddl.append(SqlKeywords.DDL.ALTER_TABLE).append(SPACE).append(tableDiff.getTableName())
                       .append(SPACE).append(SqlKeywords.DDL.ADD_COLUMN).append(SPACE)
                       .append(columnDiff.getColumnName()).append(SPACE).append(parsedFieldAdd.getDataType());
                    
                    if(DataUtils.notNull(parsedFieldAdd.getAttributes())) {
                        for (Map.Entry<String, Object> entry : parsedFieldAdd.getAttributes().entrySet()) {
                            String attributeStr = processAttribute(entry.getKey(), entry.getValue());
                            if (!attributeStr.isEmpty()) {
                                ddl.append(SPACE).append(attributeStr);
                            }
                        }
                    }
                    ddl.append(SEMICOLON).append(NEXT_LINE);
                    break;
                case MODIFIED:
                    ParsedField parsedFieldMod = parse(columnDiff.getCurrentType());
                    ddl.append("-- ").append(SqlKeywords.DDL.ALTER_TABLE).append(SPACE).append(tableDiff.getTableName()).append(NEXT_LINE);
                    ddl.append(SqlKeywords.DDL.ALTER_TABLE).append(SPACE).append(tableDiff.getTableName())
                       .append(SPACE).append("ALTER COLUMN").append(SPACE)
                       .append(columnDiff.getColumnName()).append(SPACE).append("TYPE").append(SPACE)
                       .append(parsedFieldMod.getDataType()).append(SEMICOLON).append(NEXT_LINE);
                    break;
            }
        }
        return ddl.toString();
    }

    // SQL Server DDL generation methods
    private String generateSqlServerDDL(VersionComparisonDTO versionComparison) {
        StringBuilder ddlScript = new StringBuilder();

        log.info("Start generating SQL Server DDL script");
        versionComparison.getTableDiffs().forEach(diff -> {
            switch (diff.getDiffType()) {
                case ADDED:
                    String ddl = createTableDDLSqlServer(diff);
                    ddlScript.append(ddl).append(NEXT_LINE);
                    break;
                case REMOVED:
                    String dropDdl = removeTableDDLSqlServer(diff);
                    ddlScript.append(dropDdl).append(NEXT_LINE);
                    break;
                case MODIFIED:
                    String modifyDdl = modifyTableDDLSqlServer(diff);
                    ddlScript.append(modifyDdl).append(NEXT_LINE);
                    break;
            }
        });
        return ddlScript.toString();
    }

    private String createTableDDLSqlServer(VersionComparisonDTO.TableDiff tableDiff) {
        String tableName = tableDiff.getTableName();
        StringBuilder ddl = new StringBuilder();
        
        ddl.append("-- ").append(SqlKeywords.DDL.CREATE_TABLE).append(SPACE).append(tableName).append(NEXT_LINE);
        ddl.append(SqlKeywords.DDL.CREATE_TABLE).append(SPACE).append(tableName).append(SPACE).append(OPEN_BRACKET).append(NEXT_LINE);

        for (VersionComparisonDTO.ColumnDiff columnDiff : tableDiff.getColumnDiffs()) {
            ParsedField parsedField = parse(columnDiff.getCurrentType());
            ddl.append(TAB).append(columnDiff.getColumnName()).append(SPACE).append(parsedField.getDataType());
            
            if (DataUtils.notNull(parsedField.getAttributes())) {
                for (Map.Entry<String, Object> attribute : parsedField.getAttributes().entrySet()) {
                    String attributeStr = processAttribute(attribute.getKey(), attribute.getValue());
                    if (!attributeStr.isEmpty()) {
                        ddl.append(SPACE).append(attributeStr);
                    }
                }
            }
            ddl.append(COMMA).append(NEXT_LINE);
        }
        ddl.append(CLOSE_BRACKET).append(SEMICOLON).append(NEXT_LINE);
        return ddl.toString();
    }

    private String removeTableDDLSqlServer(VersionComparisonDTO.TableDiff tableDiff) {
        StringBuilder ddl = new StringBuilder();
        ddl.append("-- ").append(SqlKeywords.DDL.DROP_TABLE).append(SPACE).append(tableDiff.getTableName()).append(NEXT_LINE);
        ddl.append(SqlKeywords.DDL.DROP_TABLE).append(SPACE).append(tableDiff.getTableName()).append(SEMICOLON).append(NEXT_LINE);
        return ddl.toString();
    }

    private String modifyTableDDLSqlServer(VersionComparisonDTO.TableDiff tableDiff) {
        StringBuilder ddl = new StringBuilder();
        
        for (VersionComparisonDTO.ColumnDiff columnDiff : tableDiff.getColumnDiffs()) {
            switch (columnDiff.getDiffType()) {
                case REMOVED:
                    ddl.append("-- ").append(SqlKeywords.DDL.ALTER_TABLE).append(SPACE).append(tableDiff.getTableName()).append(NEXT_LINE);
                    ddl.append(SqlKeywords.DDL.ALTER_TABLE).append(SPACE).append(tableDiff.getTableName())
                       .append(SPACE).append(SqlKeywords.DDL.DROP_COLUMN).append(SPACE)
                       .append(columnDiff.getColumnName()).append(SEMICOLON).append(NEXT_LINE);
                    break;
                case ADDED:
                    ParsedField parsedFieldAdd = parse(columnDiff.getCurrentType());
                    if(DataUtils.isNull(parsedFieldAdd)) throw BaseException.of(ErrorCode.PARSE_FIELD_ERROR);
                    ddl.append("-- ").append(SqlKeywords.DDL.ALTER_TABLE).append(SPACE).append(tableDiff.getTableName()).append(NEXT_LINE);
                    ddl.append(SqlKeywords.DDL.ALTER_TABLE).append(SPACE).append(tableDiff.getTableName())
                       .append(SPACE).append(SqlKeywords.DDL.ADD_COLUMN).append(SPACE)
                       .append(columnDiff.getColumnName()).append(SPACE).append(parsedFieldAdd.getDataType());
                    
                    if(DataUtils.notNull(parsedFieldAdd.getAttributes())) {
                        for (Map.Entry<String, Object> entry : parsedFieldAdd.getAttributes().entrySet()) {
                            String attributeStr = processAttribute(entry.getKey(), entry.getValue());
                            if (!attributeStr.isEmpty()) {
                                ddl.append(SPACE).append(attributeStr);
                            }
                        }
                    }
                    ddl.append(SEMICOLON).append(NEXT_LINE);
                    break;
                case MODIFIED:
                    ParsedField parsedFieldMod = parse(columnDiff.getCurrentType());
                    ddl.append("-- ").append(SqlKeywords.DDL.ALTER_TABLE).append(SPACE).append(tableDiff.getTableName()).append(NEXT_LINE);
                    ddl.append(SqlKeywords.DDL.ALTER_TABLE).append(SPACE).append(tableDiff.getTableName())
                       .append(SPACE).append("ALTER COLUMN").append(SPACE)
                       .append(columnDiff.getColumnName()).append(SPACE)
                       .append(parsedFieldMod.getDataType()).append(SEMICOLON).append(NEXT_LINE);
                    break;
            }
        }
        return ddl.toString();
    }

    /**
     * Phân tích một chuỗi định nghĩa trường thành đối tượng ParsedField.
     * @param fieldDefinition Chuỗi đầu vào, ví dụ: "varchar(255) [pk, unique, note: \"abc, xyz\"]"
     * @return một đối tượng ParsedField chứa thông tin đã phân tích.
     */
    private ParsedField parse(String fieldDefinition) {
        if (fieldDefinition == null || fieldDefinition.trim().isEmpty()) {
            return null;
        }

        fieldDefinition = fieldDefinition.trim();
        int openBracketIndex = fieldDefinition.indexOf('[');

        String dataType;
        Map<String, Object> attributes;

        if (openBracketIndex == -1) {
            dataType = fieldDefinition;
            attributes = new LinkedHashMap<>(); // Sử dụng LinkedHashMap để giữ nguyên thứ tự
        } else {
            dataType = fieldDefinition.substring(0, openBracketIndex).trim();
            int closeBracketIndex = fieldDefinition.lastIndexOf(']');
            if (closeBracketIndex > openBracketIndex) {
                String attributesString = fieldDefinition.substring(openBracketIndex + 1, closeBracketIndex);
                attributes = parseAttributesString(attributesString);
            } else {
                attributes = new LinkedHashMap<>();
            }
        }

        return new ParsedField(dataType, attributes);
    }

    /**
     * Phương thức nội bộ để phân tích chuỗi thuộc tính phức tạp.
     * Nó có thể xử lý dấu phẩy bên trong dấu ngoặc kép và ngoặc đơn.
     * Ví dụ: "pk, note: \"ghi chú, có phẩy\", default: NOW()"
     */
    private static Map<String, Object> parseAttributesString(String attributesString) {
        Map<String, Object> attributes = new LinkedHashMap<>();
        if (attributesString == null || attributesString.trim().isEmpty()) {
            return attributes;
        }

        List<String> parts = splitAttributes(attributesString);

        for (String part : parts) {
            part = part.trim();
            if (part.isEmpty()) continue;

            int colonIndex = part.indexOf(':');
            if (colonIndex == -1) {
                // Trường hợp thuộc tính không có giá trị, ví dụ: "pk", "not-null"
                attributes.put(part, true);
            } else {
                // Trường hợp thuộc tính có giá trị, ví dụ: "note: 'abc'"
                String key = part.substring(0, colonIndex).trim();
                String value = part.substring(colonIndex + 1).trim();

                // Xóa các dấu nháy đơn hoặc kép ở đầu và cuối giá trị
                if ((value.startsWith("\"") && value.endsWith("\"")) || (value.startsWith("'") && value.endsWith("'"))) {
                    value = value.substring(1, value.length() - 1);
                }
                attributes.put(key, value);
            }
        }

        return attributes;
    }

    /**
     * Tách chuỗi thuộc tính theo dấu phẩy, nhưng bỏ qua các dấu phẩy
     * nằm trong ngoặc đơn () hoặc dấu nháy "" ''.
     */
    private static List<String> splitAttributes(String str) {
        List<String> result = new ArrayList<>();
        int level = 0; // Mức độ lồng của ngoặc đơn
        boolean inDoubleQuotes = false;
        boolean inSingleQuotes = false;
        int start = 0;

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '(') level++;
            else if (c == ')') level--;
            else if (c == '"') inDoubleQuotes = !inDoubleQuotes;
            else if (c == '\'') inSingleQuotes = !inSingleQuotes;
            else if (c == ',' && level == 0 && !inDoubleQuotes && !inSingleQuotes) {
                result.add(str.substring(start, i));
                start = i + 1;
            }
        }
        result.add(str.substring(start)); // Thêm phần cuối cùng
        return result;
    }
    private String checkKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return "";
        }
        
        String upperKeyword = keyword.toUpperCase().trim();
        
        // Primary Key variants
        if (upperKeyword.equals(SqlKeywords.KeySqlConstant.PK_KEY) || 
            upperKeyword.equals(SqlKeywords.Constraints.PRIMARY_KEY) ||
            upperKeyword.equals("PRIMARY_KEY")) {
            return SqlKeywords.Constraints.PRIMARY_KEY;
        }
        
        // Not Null variants  
        if (upperKeyword.equals(SqlKeywords.Constraints.NOT_NULL) ||
            upperKeyword.equals("NOT-NULL") ||
            upperKeyword.equals("NOTNULL")) {
            return SqlKeywords.Constraints.NOT_NULL;
        }
        
        // Unique constraint
        if (upperKeyword.equals(SqlKeywords.Constraints.UNIQUE)) {
            return SqlKeywords.Constraints.UNIQUE;
        }
        
        // Default constraint
        if (upperKeyword.equals(SqlKeywords.Constraints.DEFAULT)) {
            return SqlKeywords.Constraints.DEFAULT;
        }
        
        // Index
        if (upperKeyword.equals(SqlKeywords.Constraints.INDEX)) {
            return SqlKeywords.Constraints.INDEX;
        }
        
        // Foreign Key and References
        if (upperKeyword.equals(SqlKeywords.Constraints.REFERENCES) ||
            upperKeyword.equals(SqlKeywords.Constraints.FOREIGN_KEY) ||
            upperKeyword.equals("FOREIGN_KEY") ||
            upperKeyword.equals("FK")) {
            return SqlKeywords.Constraints.REFERENCES;
        }
        
        // Check constraint
        if (upperKeyword.equals(SqlKeywords.Constraints.CHECK)) {
            return SqlKeywords.Constraints.CHECK;
        }
        
        // Auto Increment variants
        if (upperKeyword.equals("AUTO_INCREMENT") ||
            upperKeyword.equals("AUTOINCREMENT") ||
            upperKeyword.equals("IDENTITY") ||
            upperKeyword.equals("INCREMENT") ||
            upperKeyword.equals("SERIAL")) {
            return "AUTO_INCREMENT";
        }
        
        // Null constraint
        if (upperKeyword.equals("NULL") ||
            upperKeyword.equals("NULLABLE")) {
            return "NULL";
        }
        
        // Common database-specific keywords
        switch (upperKeyword) {
            // MySQL specific
            case "UNSIGNED":
                return "UNSIGNED";
            case "ZEROFILL":
                return "ZEROFILL";
            case "BINARY":
                return "BINARY";
            case "ASCII":
                return "ASCII";
            case "UNICODE":
                return "UNICODE";
                
            // Oracle specific  
            case "ENABLE":
                return "ENABLE";
            case "DISABLE":
                return "DISABLE";
            case "VALIDATE":
                return "VALIDATE";
            case "NOVALIDATE":
                return "NOVALIDATE";
                
            // PostgreSQL specific
            case "GENERATED":
                return "GENERATED";
            case "ALWAYS":
                return "ALWAYS";
            case "BY":
                return "BY";
            case "STORED":
                return "STORED";
            case "VIRTUAL":
                return "VIRTUAL";
                
            // SQL Server specific
            case "ROWGUIDCOL":
                return "ROWGUIDCOL";
            case "SPARSE":
                return "SPARSE";
            case "FILESTREAM":
                return "FILESTREAM";
                
            // Common constraints and modifiers
            case "COMMENT":
                return "COMMENT";
            case "COLLATE":
                return "COLLATE";
            case "CHARACTER":
                return "CHARACTER";
            case "SET":
                return "SET";
            case "ON":
                return "ON";
            case "UPDATE":
                return "ON UPDATE";
            case "DELETE":
                return "ON DELETE";
            case "CASCADE":
                return "CASCADE";
            case "RESTRICT":
                return "RESTRICT";
            case "NO":
                return "NO";
            case "ACTION":
                return "ACTION";
                
            // For keywords with values (like note, default with values)
            case "NOTE":
                return ""; // Notes are usually handled differently
                
            default:
                // Instead of throwing exception, log warning and return the keyword as-is
                log.warn("Unknown SQL keyword encountered: {}. Using as-is.", keyword);
                return keyword.toUpperCase();
        }
    }

    /**
     * Process attribute with its value for DDL generation
     * @param key attribute name
     * @param value attribute value  
     * @return formatted DDL string for the attribute
     */
    private String processAttribute(String key, Object value) {
        if (key == null || key.trim().isEmpty()) {
            return "";
        }
        
        String upperKey = key.toUpperCase().trim();
        String processedKey = checkKeyword(key);
        
        // Handle attributes that don't need values in DDL
        if (processedKey.equals(SqlKeywords.Constraints.PRIMARY_KEY) ||
            processedKey.equals(SqlKeywords.Constraints.NOT_NULL) ||
            processedKey.equals(SqlKeywords.Constraints.UNIQUE) ||
            processedKey.equals("AUTO_INCREMENT") ||
            processedKey.equals("UNSIGNED") ||
            processedKey.equals("ZEROFILL")) {
            return processedKey;
        }
        
        // Handle attributes that need values
        if (value != null && !value.toString().trim().isEmpty()) {
            String valueStr = value.toString().trim();
            
            switch (upperKey) {
                case "DEFAULT":
                    return SqlKeywords.Constraints.DEFAULT + SPACE + valueStr;
                case "COMMENT":
                    return "COMMENT" + SPACE + SINGLE_QUOTE + valueStr + SINGLE_QUOTE;
                case "NOTE":
                    // Notes are typically not included in DDL, return empty
                    return "";
                case "REF":
                case "REFERENCES":
                    return SqlKeywords.Constraints.REFERENCES + SPACE + valueStr;
                case "CHECK":
                    return SqlKeywords.Constraints.CHECK + SPACE + OPEN_BRACKET + valueStr + CLOSE_BRACKET;
                case "COLLATE":
                    return "COLLATE" + SPACE + valueStr;
                default:
                    // For other attributes with values, include both key and value
                    if (Boolean.TRUE.equals(value)) {
                        return processedKey;
                    } else {
                        return processedKey + SPACE + valueStr;
                    }
            }
        }
        
        // If no value but the key is valid, return just the key
        if (!processedKey.isEmpty()) {
            return processedKey;
        }
        
        return "";
    }

}

