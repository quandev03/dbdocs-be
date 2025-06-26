package com.vissoft.vn.dbdocs.domain.service.impl;

import com.vissoft.vn.dbdocs.domain.model.dbml.*;
import com.vissoft.vn.dbdocs.domain.service.DbmlParserService;
import com.vissoft.vn.dbdocs.infrastructure.exception.BaseException;
import com.vissoft.vn.dbdocs.infrastructure.exception.ErrorCode;
import com.vissoft.vn.dbdocs.infrastructure.util.DataUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class DbmlParserServiceImpl implements DbmlParserService {

    @Override
    public DbmlModel parseDbml(String dbmlContent) {
        log.info("Starting DBML parsing process");
        if (DataUtils.isNull(dbmlContent) || dbmlContent.trim().isEmpty()) {
            log.warn("DBML content is empty or null, returning empty model");
            return new DbmlModel();
        }
        
        try {
            log.debug("Initializing DBML model");
            DbmlModel model = new DbmlModel();
            
            // Analyze the DBML content line by line
            log.debug("Splitting content into lines for analysis");
            String[] lines = dbmlContent.split("\n");
            log.debug("Total lines to process: {}", lines.length);

            parseDbmlContent(lines, model);
            
            log.info("DBML parsing completed successfully. Found {} tables, {} enums, {} refs", 
                    model.getTables().size(), 
                    model.getEnums().size(),
                    model.getRefs().size());
            
            return model;
        } catch (StringIndexOutOfBoundsException e) {
            log.error("String index error during DBML parsing. Possible malformed DBML syntax: {}", e.getMessage(), e);
            throw BaseException.of(ErrorCode.ERROR_PARSING_DBML);
        } catch (Exception e) {
            log.error("Unexpected error during DBML parsing: {}", e.getMessage(), e);
            throw BaseException.of(ErrorCode.ERROR_PARSING_DBML);
        }
    }

    private void parseDbmlContent(String[] lines, DbmlModel model) {
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            log.trace("Processing line {}: {}", i + 1, line);

            // Skip empty lines and comments
            if (line.isEmpty() || line.startsWith("//") || line.startsWith("/*")) {
                continue;
            }

            // Project definition
            if (line.startsWith("Project") && line.contains("{")) {
                log.debug("Found Project definition at line {}", i + 1);
                String projectName = extractProjectName(line);
                model.setProjectName(projectName);
                log.debug("Set project name: {}", projectName);
                continue;
            }

            // Table definition
            if (line.startsWith("Table") && line.contains("{")) {
                log.debug("Found Table definition at line {}", i + 1);
                TableModel table = parseTable(lines, i, model);
                if (table != null) {
                    model.getTables().add(table);
                    log.debug("Added table to model: {}", table.getName());
                }
                // Skip to end of table definition
                while (i < lines.length && !lines[i].trim().equals("}")) {
                    i++;
                }
                continue;
            }

            // Reference definition
            if (line.startsWith("Ref:")) {
                log.debug("Found Reference definition at line {}", i + 1);
                RefModel ref = parseReference(line);
                if (ref != null) {
                    model.getRefs().add(ref);
                    log.debug("Added reference to model");
                }
                continue;
            }

            // Enum definition
            if (line.startsWith("Enum") && line.contains("{")) {
                log.debug("Found Enum definition at line {}", i + 1);
                EnumModel enumModel = parseEnum(lines, i);
                if (enumModel != null) {
                    model.getEnums().add(enumModel);
                    log.debug("Added enum to model: {}", enumModel.getName());
                }
                // Skip to end of enum definition
                while (i < lines.length && !lines[i].trim().equals("}")) {
                    i++;
                }
                continue;
            }
        }
    }

    private String extractProjectName(String line) {
        String projectName = line.substring(7, line.indexOf("{")).trim();
        // Remove quotes if present
        if ((projectName.startsWith("'") && projectName.endsWith("'")) ||
            (projectName.startsWith("\"") && projectName.endsWith("\""))) {
            projectName = projectName.substring(1, projectName.length() - 1);
        }
        return projectName;
    }

    private TableModel parseTable(String[] lines, int startIndex, DbmlModel model) {
        String line = lines[startIndex].trim();
        TableModel table = new TableModel();
        
        // Extract table name
        String tableName = line.substring(5, line.indexOf("{")).trim();
        if (tableName.contains(" as ")) {
            String[] parts = tableName.split(" as ");
            table.setName(parts[0].trim());
            table.setAlias(parts[1].trim());
        } else {
            table.setName(tableName);
        }

        // Parse table contents
        List<ColumnModel> columns = new ArrayList<>();
        int i = startIndex + 1;
        
        while (i < lines.length) {
            String currentLine = lines[i].trim();
            
            // End of table
            if (currentLine.equals("}")) {
                break;
            }
            
            // Skip empty lines and comments
            if (currentLine.isEmpty() || currentLine.startsWith("//")) {
                i++;
                continue;
            }
            
            // Parse column definition
            ColumnModel column = parseColumn(currentLine);
            if (column != null) {
                columns.add(column);
                log.trace("Added column to table {}: {}", table.getName(), column.getName());
            }
            
            i++;
        }
        
        table.setColumns(columns);
        return table;
    }

    private ColumnModel parseColumn(String line) {
        try {
            // Pattern to match: columnName dataType [attributes]
            Pattern pattern = Pattern.compile("^(\\w+)\\s+([^\\[\\s]+(?:\\([^)]*\\))?)(?:\\s*\\[([^\\]]+)\\])?");
            Matcher matcher = pattern.matcher(line);
            
            if (!matcher.matches()) {
                log.warn("Could not parse column definition: {}", line);
                return null;
            }
            
            String columnName = matcher.group(1);
            String dataType = matcher.group(2);
            String attributesStr = matcher.group(3);
            
            ColumnModel column = ColumnModel.builder()
                    .name(columnName)
                    .dataType(extractDataType(dataType))
                    .typeParam(extractTypeParam(dataType))
                    .build();
            
            // Parse attributes if present
            if (attributesStr != null && !attributesStr.trim().isEmpty()) {
                parseColumnAttributes(column, attributesStr);
            }
            
            return column;
        } catch (Exception e) {
            log.warn("Error parsing column: {} - {}", line, e.getMessage());
            return null;
        }
    }

    private String extractDataType(String typeStr) {
        int parenIndex = typeStr.indexOf('(');
        if (parenIndex > 0) {
            return typeStr.substring(0, parenIndex);
        }
        return typeStr;
    }

    private String extractTypeParam(String typeStr) {
        int parenIndex = typeStr.indexOf('(');
        if (parenIndex > 0 && typeStr.endsWith(")")) {
            return typeStr.substring(parenIndex + 1, typeStr.length() - 1);
        }
        return null;
    }

    private void parseColumnAttributes(ColumnModel column, String attributesStr) {
        String[] attributes = attributesStr.split(",");
        
        for (String attr : attributes) {
            attr = attr.trim();
            
            if (attr.equals("pk") || attr.equals("primary key")) {
                column.setPrimaryKey(true);
            } else if (attr.equals("unique")) {
                column.setUnique(true);
            } else if (attr.equals("not null")) {
                column.setNotNull(true);
            } else if (attr.equals("increment")) {
                column.setAutoIncrement(true);
            } else if (attr.startsWith("default:")) {
                String defaultValue = attr.substring(8).trim();
                // Remove quotes if present
                if ((defaultValue.startsWith("'") && defaultValue.endsWith("'")) ||
                    (defaultValue.startsWith("\"") && defaultValue.endsWith("\""))) {
                    defaultValue = defaultValue.substring(1, defaultValue.length() - 1);
                }
                column.setDefaultValue(defaultValue);
            } else if (attr.startsWith("note:")) {
                String note = attr.substring(5).trim();
                // Remove quotes if present
                if ((note.startsWith("'") && note.endsWith("'")) ||
                    (note.startsWith("\"") && note.endsWith("\""))) {
                    note = note.substring(1, note.length() - 1);
                }
                column.setNote(note);
            } else if (attr.startsWith("ref:")) {
                String refStr = attr.substring(4).trim();
                ColumnModel.RefValue ref = parseColumnReference(refStr);
                column.setReference(ref);
            }
        }
    }

    private ColumnModel.RefValue parseColumnReference(String refStr) {
        try {
            // Pattern: tableName.columnName or tableName.columnName >
            Pattern pattern = Pattern.compile("([\\w_]+)\\.([\\w_]+)\\s*([<>-]?)");
            Matcher matcher = pattern.matcher(refStr);
            
            if (matcher.matches()) {
                return ColumnModel.RefValue.builder()
                        .tableName(matcher.group(1))
                        .columnName(matcher.group(2))
                        .cardinality(matcher.group(3).isEmpty() ? null : matcher.group(3))
                        .build();
            }
        } catch (Exception e) {
            log.warn("Error parsing reference: {} - {}", refStr, e.getMessage());
        }
        return null;
    }

    private RefModel parseReference(String line) {
        // TODO: Implement full reference parsing
        // For now, return null as references are handled at column level
        return null;
    }

    private EnumModel parseEnum(String[] lines, int startIndex) {
        // TODO: Implement enum parsing if needed
        return null;
    }
} 