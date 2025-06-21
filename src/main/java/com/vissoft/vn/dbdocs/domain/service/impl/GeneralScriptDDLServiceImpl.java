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
            case Constants.SQL.Dialect.MYSQL, Constants.SQL.Dialect.MARIADB->{
                return generateMysqlDDL(versionComparison);
            }
            case Constants.SQL.Dialect.POSTGRESQL->{
                return null;
            }
            case Constants.SQL.Dialect.ORACLE -> {
                return generateOracleDDL(versionComparison);
            }
            case Constants.SQL.Dialect.SQL_SERVER->{
                return null;
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
        // Implement Oracle DDL generation logic here

        StringBuilder ddlScript = new StringBuilder();

        log.info("Start generating Oracle DDL script");
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
        // Implement logic to create DDL for table creation
        StringBuilder ddl = new StringBuilder();
        ddl.append("-- ").append(SqlKeywords.DDL.CREATE_TABLE).append(SPACE).append(tableDiff.getTableName()).append(NEXT_LINE);
        ddl.append(SqlKeywords.DDL.CREATE_TABLE).append(" ").append(tableDiff.getTableName()).append(" (");
        for (VersionComparisonDTO.ColumnDiff columnDiff : tableDiff.getColumnDiffs()) {
            ParsedField parsedField = parse(columnDiff.getCurrentType());
            ddl.append(columnDiff.getColumnName()).append(SPACE).append(parsedField.getDataType());
            // Append attributes if any
            if(DataUtils.notNull(parsedField.getAttributes())) {
                for (Map.Entry<String, Object> entry : parsedField.getAttributes().entrySet()) {
                    String key = checkKeyword(entry.getKey());

                    ddl.append(SPACE).append(key).append(SPACE);
                }
            }
            ddl.append(COMMA).append(NEXT_LINE);
            log.info("Column name: {}, column type: {}", columnDiff.getColumnName(), columnDiff.getColumnName());
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
        // Implement logic to create DDL for table modification
        StringBuilder ddl = new StringBuilder();

        // generate DDL for adding columns
        StringBuilder ddlModifyTypeAdd = new StringBuilder();
        ddlModifyTypeAdd.append("-- ").append(SqlKeywords.DDL.ALTER_TABLE).append(SPACE).append(tableDiff.getTableName()).append(NEXT_LINE);
        ddlModifyTypeAdd.append(SqlKeywords.DDL.ALTER_TABLE).append(SPACE).append(tableDiff.getTableName()).append(SPACE);
        ddlModifyTypeAdd.append(SqlKeywords.ModifiersType.ADD).append(SPACE).append(OPEN_BRACKET).append(NEXT_LINE);

        // generate DDL for removing columns
        StringBuilder ddlModifyTypeRemove = new StringBuilder();
        ddlModifyTypeRemove.append("-- ").append(SqlKeywords.DDL.ALTER_TABLE).append(SPACE).append(tableDiff.getTableName()).append(NEXT_LINE);
        // generate DDL for modifying columns
        StringBuilder ddlModifyTypeModify = new StringBuilder();
        ddlModifyTypeModify.append("-- ").append(SqlKeywords.DDL.ALTER_TABLE).append(SPACE).append(tableDiff.getTableName()).append(NEXT_LINE);
        ddlModifyTypeModify.append(SqlKeywords.DDL.ALTER_TABLE).append(SPACE).append(tableDiff.getTableName()).append(SPACE);
        ddlModifyTypeModify.append(SqlKeywords.ModifiersType.MODIFY).append(SPACE).append(OPEN_BRACKET).append(NEXT_LINE);
        int countRemove = 0;
        int countModify = 0;
        int countAdd = 0;

        for (VersionComparisonDTO.ColumnDiff columnDiff : tableDiff.getColumnDiffs()) {
            // filter type modified
            if(Objects.equals(columnDiff.getDiffType().name(), VersionComparisonDTO.DiffType.REMOVED.name())) {
                countRemove++;
                ddlModifyTypeRemove.append(SqlKeywords.DDL.ALTER_TABLE)
                        .append(SPACE)
                        .append(tableDiff.getTableName())
                        .append(SPACE)
                        .append(SqlKeywords.DDL.DROP_COLUMN)
                        .append(SPACE)
                        .append(columnDiff.getColumnName())
                        .append(SEMICOLON)
                        .append(NEXT_LINE);
            }
            else if(Objects.equals(columnDiff.getDiffType().name(), VersionComparisonDTO.DiffType.ADDED.name())) {
                countAdd++;
                ParsedField parsedField = parse(columnDiff.getCurrentType());
                ddlModifyTypeAdd.append(columnDiff.getColumnName()).append(SPACE).append(parsedField.getDataType());
                // Append attributes if any
                if(DataUtils.notNull(parsedField.getAttributes())) {
                    for (Map.Entry<String, Object> entry : parsedField.getAttributes().entrySet()) {
                        String key = checkKeyword(entry.getKey());

                        ddlModifyTypeAdd.append(SPACE).append(key).append(SPACE);
                    }
                }
                ddlModifyTypeAdd.append(SEMICOLON).append(NEXT_LINE);
            } else if(Objects.equals(columnDiff.getDiffType().name(), VersionComparisonDTO.DiffType.MODIFIED.name())) {
                countModify++;
                ParsedField parsedField = parse(columnDiff.getCurrentType());
                ddlModifyTypeModify.append(columnDiff.getColumnName()).append(SPACE).append(parsedField.getDataType()).append(SPACE);
                if(DataUtils.notNull(parsedField.getAttributes())) {
                    for (Map.Entry<String, Object> entry : parsedField.getAttributes().entrySet()) {
                        String key = checkKeyword(entry.getKey());
                        ddlModifyTypeModify.append(SPACE).append(key).append(SPACE);
                    }
                }
                ddlModifyTypeModify.append(SEMICOLON).append(NEXT_LINE);
            }
        }

        ddlModifyTypeAdd.append(CLOSE_BRACKET).append(NEXT_LINE);
        ddlModifyTypeModify.append(CLOSE_BRACKET).append(NEXT_LINE);
        if(countRemove > 0) {
            ddl.append(ddlModifyTypeRemove).append(NEXT_LINE);
        }
        if (countAdd > 0) {
            ddl.append(ddlModifyTypeAdd).append(NEXT_LINE);
        }
        if(countModify > 0) {
            ddl.append(ddlModifyTypeModify).append(NEXT_LINE);
        }
        ddl.append("-- END");
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
            StringBuilder columnBuilder = new StringBuilder(SPACE);
            ParsedField parsedField = parse(columnDiff.getCurrentType());

            // Thêm tên cột và kiểu dữ liệu
            columnBuilder.append(columnDiff.getColumnName()).append(SPACE).append(parsedField.getDataType());

            // Xử lý các thuộc tính (attributes)
            if (DataUtils.notNull(parsedField.getAttributes())) {
                for (Map.Entry<String, Object> attribute : parsedField.getAttributes().entrySet()) {
                    String key = checkKeyword(attribute.getKey());
                    ddl.append(SPACE).append(key);
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

        // generate DDL for adding columns
        StringBuilder ddlModifyTypeAdd = new StringBuilder();
        ddlModifyTypeAdd.append("-- ").append(SqlKeywords.DDL.ALTER_TABLE).append(SPACE).append(tableDiff.getTableName()).append(NEXT_LINE);
        ddlModifyTypeAdd.append(SqlKeywords.DDL.ALTER_TABLE).append(SPACE).append(tableDiff.getTableName()).append(SPACE);
        ddlModifyTypeAdd.append(SqlKeywords.ModifiersType.ADD).append(SPACE).append(NEXT_LINE);

        // generate DDL for removing columns
        StringBuilder ddlModifyTypeRemove = new StringBuilder();
        ddlModifyTypeRemove.append("-- ").append(SqlKeywords.DDL.ALTER_TABLE).append(SPACE).append(tableDiff.getTableName()).append(NEXT_LINE);
        // generate DDL for modifying columns
        StringBuilder ddlModifyTypeModify = new StringBuilder();
        ddlModifyTypeModify.append("-- ").append(SqlKeywords.DDL.ALTER_TABLE).append(SPACE).append(tableDiff.getTableName()).append(NEXT_LINE);
        ddlModifyTypeModify.append(SqlKeywords.DDL.ALTER_TABLE).append(SPACE).append(tableDiff.getTableName()).append(SPACE);
        ddlModifyTypeModify.append(SqlKeywords.ModifiersType.MODIFY).append(SPACE).append(OPEN_BRACKET).append(NEXT_LINE);
        int countRemove = 0;
        int countModify = 0;
        int countAdd = 0;

        for (VersionComparisonDTO.ColumnDiff columnDiff : tableDiff.getColumnDiffs()) {
            // filter type modified
            if(Objects.equals(columnDiff.getDiffType().name(), VersionComparisonDTO.DiffType.REMOVED.name())) {
                countRemove++;
                ddlModifyTypeRemove.append(SqlKeywords.DDL.ALTER_TABLE)
                        .append(SPACE)
                        .append(tableDiff.getTableName())
                        .append(SPACE)
                        .append(SqlKeywords.DDL.DROP_COLUMN)
                        .append(SPACE)
                        .append(columnDiff.getColumnName())
                        .append(SEMICOLON)
                        .append(NEXT_LINE);
            }
            else if(Objects.equals(columnDiff.getDiffType().name(), VersionComparisonDTO.DiffType.ADDED.name())) {
                countAdd++;
                ParsedField parsedField = parse(columnDiff.getCurrentType());
                if(DataUtils.isNull(parsedField)) throw BaseException.of(ErrorCode.PARSE_FIELD_ERROR);
                ddlModifyTypeAdd.append(TAB).append(SqlKeywords.DDL.ADD_COLUMN).append(SPACE)
                        .append(columnDiff.getColumnName()).append(SPACE)
                        .append(parsedField.getDataType());
                // Append attributes if any
                if(DataUtils.notNull(parsedField.getAttributes())) {
                    for (Map.Entry<String, Object> entry : parsedField.getAttributes().entrySet()) {
                        String key = checkKeyword(entry.getKey());
                        ddlModifyTypeAdd.append(SPACE).append(key).append(SPACE);
                    }
                }
                ddlModifyTypeAdd.append(SEMICOLON).append(NEXT_LINE);
            } else if(Objects.equals(columnDiff.getDiffType().name(), VersionComparisonDTO.DiffType.MODIFIED.name())) {
                countModify++;
                ParsedField parsedField = parse(columnDiff.getCurrentType());
                ddlModifyTypeModify.append(TAB).append(columnDiff.getColumnName()).append(SPACE).append(parsedField.getDataType()).append(SPACE);
                if(DataUtils.notNull(parsedField.getAttributes())) {
                    for (Map.Entry<String, Object> entry : parsedField.getAttributes().entrySet()) {
                        String key = checkKeyword(entry.getKey());
                        ddlModifyTypeModify.append(SPACE).append(key).append(SPACE);
                    }
                }
                ddlModifyTypeModify.append(SEMICOLON).append(NEXT_LINE);
            }
        }

        ddlModifyTypeAdd.append(NEXT_LINE);
        ddlModifyTypeModify.append(CLOSE_BRACKET).append(NEXT_LINE);
        if(countRemove > 0) {
            ddl.append(ddlModifyTypeRemove).append(NEXT_LINE);
        }
        if (countAdd > 0) {
            ddl.append(ddlModifyTypeAdd).append(NEXT_LINE);
        }
        if(countModify > 0) {
            ddl.append(ddlModifyTypeModify).append(NEXT_LINE);
        }
        ddl.append("-- END");
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
        switch (keyword.toUpperCase()) {
            case SqlKeywords.KeySqlConstant.PK_KEY, SqlKeywords.Constraints.PRIMARY_KEY -> {
                return SqlKeywords.Constraints.PRIMARY_KEY;
            }
            case SqlKeywords.Constraints.NOT_NULL -> {
                return SqlKeywords.Constraints.NOT_NULL;
            }
            case SqlKeywords.Constraints.DEFAULT -> {
                return SqlKeywords.Constraints.DEFAULT;
            }
            case SqlKeywords.Constraints.INDEX -> {
                return SqlKeywords.Constraints.INDEX;
            }
            case SqlKeywords.Constraints.REFERENCES -> {
                return SqlKeywords.Constraints.REFERENCES;
            }
            case SqlKeywords.Constraints.UNIQUE -> {
                return SqlKeywords.Constraints.UNIQUE;
            }
            default -> throw BaseException.of(ErrorCode.KEYWORD_NOT_ALLOWED);

        }
    }

}
