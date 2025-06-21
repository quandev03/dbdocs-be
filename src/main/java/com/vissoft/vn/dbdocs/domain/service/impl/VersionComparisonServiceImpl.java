package com.vissoft.vn.dbdocs.domain.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vissoft.vn.dbdocs.application.dto.VersionComparisonDTO;
import com.vissoft.vn.dbdocs.domain.entity.ChangeLog;
import com.vissoft.vn.dbdocs.domain.entity.Version;
import com.vissoft.vn.dbdocs.domain.exception.CustomException;
import com.vissoft.vn.dbdocs.domain.model.DbmlParser;
import com.vissoft.vn.dbdocs.domain.model.dbml.ColumnModel;
import com.vissoft.vn.dbdocs.domain.model.dbml.DbmlModel;
import com.vissoft.vn.dbdocs.domain.model.dbml.TableModel;
import com.vissoft.vn.dbdocs.domain.repository.ChangeLogRepository;
import com.vissoft.vn.dbdocs.domain.repository.VersionRepository;
import com.vissoft.vn.dbdocs.domain.service.VersionComparisonService;
import com.vissoft.vn.dbdocs.infrastructure.util.DataUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javers.core.Javers;
import org.javers.core.metamodel.object.GlobalId;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

// Import cho các thay đổi của Map (quan trọng nhất cho lỗi này)
import org.javers.core.diff.Change; // Lớp Change cơ sở


@Service
@RequiredArgsConstructor
@Slf4j
public class VersionComparisonServiceImpl implements VersionComparisonService {
    private final VersionRepository versionRepository;
    private final ChangeLogRepository changeLogRepository;
    private final ObjectMapper objectMapper;
    private final Javers javers;

    @Override
    public VersionComparisonDTO compareVersions(String projectId, Integer fromVersion, Integer toVersion) {
        log.info("Comparing versions {} and {} for project {}", fromVersion, toVersion, projectId);
        
        // Check fromVersion and toVersion validity
        if (DataUtils.notNull(toVersion) && fromVersion >= toVersion) {
            throw new CustomException("From version must be less than to version", HttpStatus.BAD_REQUEST);
        }
        
        // Find the Version entities for fromVersion and toVersion
        Optional<Version> fromVersionEntity = versionRepository.findByProjectIdAndCodeVersion(projectId, fromVersion);
        Optional<Version> toVersionEntity;
        if (DataUtils.notNull(toVersion)) {
            // If toVersion is specified, find the corresponding Version entity
            toVersionEntity = versionRepository.findByProjectIdAndCodeVersion(projectId, toVersion);
        } else {
            // If toVersion is null, use the latest changelog for comparison
            log.info("toVersion is null, using the latest changelog for comparison");
            toVersionEntity = Optional.empty(); // Not needed for the latest changelog
        }
        if (fromVersionEntity.isEmpty() || (DataUtils.notNull(toVersion) && toVersionEntity.isEmpty())) {
            throw new CustomException("One or both versions not found", HttpStatus.NOT_FOUND);
        }
        
        // Get the ChangeLog for fromVersion
        String fromChangeLogId = fromVersionEntity.get().getChangeLogId();
        Optional<ChangeLog> fromChangeLog = changeLogRepository.findById(fromChangeLogId);
        
        Optional<ChangeLog> toChangeLog;
        if (DataUtils.notNull(toVersion)) {
            // If toVersion is specified, get the ChangeLog for that version
            String toChangeLogId = toVersionEntity.get().getChangeLogId();
            toChangeLog = changeLogRepository.findById(toChangeLogId);
        } else {
            // If toVersion is null, get the latest ChangeLog for the project
            toChangeLog = changeLogRepository.findLatestChangeLogByProjectId(projectId);
        }
        
        if (fromChangeLog.isEmpty() || toChangeLog.isEmpty()) {
            throw new CustomException("One or both changelogs not found", HttpStatus.NOT_FOUND);
        }
        
        // Get the DBML content from the ChangeLogs
        String fromDbml = fromChangeLog.get().getContent();
        String toDbml = toChangeLog.get().getContent();
        
        // Parse the DBML content to create DbmlModel objects
        DbmlParser parser = new DbmlParser();
        
        try {
            DbmlModel beforeModel = parser.parse(fromDbml);
            DbmlModel currentModel = parser.parse(toDbml);
            
            // Handle case where no tables exist in either model
            Map<String, Object> diffChanges = compareModels(beforeModel, currentModel);
            List<VersionComparisonDTO.TableDiff> tableDiffs = createTableDiffs(diffChanges, objectMapper);
            String diffJson = objectMapper.writeValueAsString(diffChanges);
            log.info("Version comparison result: {}", diffJson);


            
            // If toVersion is null, we assume the next version is fromVersion + 1
            Integer actualToVersion = toVersion;
            if (DataUtils.isNull(toVersion)) {
                actualToVersion = fromVersion + 1;
            }
            
            // Save diff to the toVersion entity if it exists
            if (DataUtils.notNull(toVersion)) {
                toVersionEntity.get().setDiffChange(diffJson);
                versionRepository.save(toVersionEntity.get());
            }
            
            return VersionComparisonDTO.builder()
                    .projectId(projectId)
                    .fromVersion(fromVersion)
                    .toVersion(actualToVersion)
                    .diffChanges(diffJson)
                    .tableDiffs(tableDiffs)
                    .build();
        } catch (Exception e) {
            log.error("Error parsing DBML or comparing versions", e);
            throw new CustomException("Error parsing DBML or comparing versions: " + e.getMessage(), 
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Bạn không cần các hàm helper extract...FromGlobalId nữa với cách làm này.

    private Map<String, Object> compareModels(DbmlModel beforeModel, DbmlModel currentModel) {
        log.debug("Comparing models using direct, manual diffing logic for clarity.");
        try {
            // Tạo Map cho hai phiên bản
            Map<String, TableModel> beforeTablesMap = beforeModel.getTables().stream()
                    .collect(Collectors.toMap(TableModel::getName, table -> table, (t1, t2) -> t1));
            Map<String, TableModel> currentTablesMap = currentModel.getTables().stream()
                    .collect(Collectors.toMap(TableModel::getName, table -> table, (t1, t2) -> t1));

            ObjectNode rootNode = objectMapper.createObjectNode();
            Map<String, ArrayNode> tableChanges = new HashMap<>();
            ArrayNode addedTables = objectMapper.createArrayNode();
            ArrayNode removedTables = objectMapper.createArrayNode();

            // --- Xử lý THÊM BẢNG ---
            for (String currentTableName : currentTablesMap.keySet()) {
                if (!beforeTablesMap.containsKey(currentTableName)) {
                    // Lấy toàn bộ đối tượng TableModel của bảng mới
                    TableModel addedTableObject = currentTablesMap.get(currentTableName);

                    // Chuyển đổi đối tượng này thành dạng JSON và thêm vào mảng
                    // Giờ đây "addedTables" sẽ là một mảng các đối tượng bảng, không phải mảng chuỗi
                    addedTables.add(objectMapper.valueToTree(addedTableObject));
                }
            }

            // --- Xử lý XÓA BẢNG ---
            for (String beforeTableName : beforeTablesMap.keySet()) {
                if (!currentTablesMap.containsKey(beforeTableName)) {
                    removedTables.add(beforeTableName);
                }
            }

            // --- Xử lý SỬA BẢNG ---
            for (String tableName : currentTablesMap.keySet()) {
                if (beforeTablesMap.containsKey(tableName)) {
                    TableModel beforeTable = beforeTablesMap.get(tableName);
                    TableModel currentTable = currentTablesMap.get(tableName);

                    // Nếu hai phiên bản của bảng không bằng nhau (nhờ có .equals())
                    if (!beforeTable.equals(currentTable)) {
                        ArrayNode changesArray = tableChanges.computeIfAbsent(tableName, k -> objectMapper.createArrayNode());

                        // So sánh thủ công danh sách cột
                        Map<String, ColumnModel> beforeColumns = beforeTable.getColumns().stream()
                                .collect(Collectors.toMap(ColumnModel::getName, col -> col));
                        Map<String, ColumnModel> currentColumns = currentTable.getColumns().stream()
                                .collect(Collectors.toMap(ColumnModel::getName, col -> col));

                        // Tìm cột bị xóa
                        beforeColumns.keySet().stream()
                                .filter(colName -> !currentColumns.containsKey(colName))
                                .forEach(removedColName -> {
                                    ObjectNode changeNode = objectMapper.createObjectNode();
                                    changeNode.put("property", "column");
                                    changeNode.put("changeType", "REMOVED");
                                    changeNode.set("value", objectMapper.valueToTree(beforeColumns.get(removedColName)));
                                    changesArray.add(changeNode);
                                });

                        // Tìm cột được thêm hoặc sửa
                        currentColumns.forEach((colName, currentCol) -> {
                            if (!beforeColumns.containsKey(colName)) {
                                // Cột mới
                                ObjectNode changeNode = objectMapper.createObjectNode();
                                changeNode.put("property", "column");
                                changeNode.put("changeType", "ADDED");
                                changeNode.set("value", objectMapper.valueToTree(currentCol));
                                changesArray.add(changeNode);
                            } else {
                                ColumnModel beforeCol = beforeColumns.get(colName);
                                // Nếu cột bị sửa đổi (nhờ có .equals() trên ColumnModel)
                                if (!currentCol.equals(beforeCol)) {
                                    ObjectNode changeNode = objectMapper.createObjectNode();
                                    changeNode.put("property", "column");
                                    changeNode.put("changeType", "MODIFIED");
                                    changeNode.set("oldValue", objectMapper.valueToTree(beforeCol));
                                    changeNode.set("newValue", objectMapper.valueToTree(currentCol));
                                    changesArray.add(changeNode);
                                }
                            }
                        });
                    }
                }
            }

            rootNode.set("addedTables", addedTables);
            rootNode.set("removedTables", removedTables);
            ObjectNode tableDetailsNode = objectMapper.createObjectNode();
            tableChanges.forEach(tableDetailsNode::set);
            rootNode.set("tableChanges", tableDetailsNode);

            return objectMapper.convertValue(rootNode, Map.class);

        } catch (Exception e) {
            log.error("Error during manual comparison: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    // Bạn sẽ cần các hàm helper mới để trích xuất thông tin từ GlobalId
    /**
     * Helper mới, sử dụng globalId.toString() để trích xuất tên bảng từ GlobalId.
     * Nó lấy ra số index của bảng từ chuỗi ID, sau đó dùng index để tra cứu tên bảng
     * trong model hiện tại (currentModel).
     */
    private String extractTableNameFromGlobalId(GlobalId globalId, DbmlModel currentModel) {
        // SỬA LỖI: Dùng toString() thay vì getPath()
        String fullIdString = globalId.toString();

        // Regex để tìm chuỗi "tables/" theo sau bởi một hoặc nhiều chữ số (là table index)
        Pattern pattern = Pattern.compile("tables/(\\d+)");
        Matcher matcher = pattern.matcher(fullIdString);

        if (matcher.find()) {
            int tableIndex = Integer.parseInt(matcher.group(1));
            if (tableIndex < currentModel.getTables().size()) {
                return currentModel.getTables().get(tableIndex).getName();
            }
        }
        // Nếu không tìm thấy, có thể là một thay đổi ở cấp model, không thuộc bảng nào
        return null;
    }

    /**
     * Helper mới, sử dụng globalId.toString() để trích xuất tên cột bị ảnh hưởng.
     * Nó hoạt động bằng cách lấy index của bảng và cột, sau đó tra cứu trong model
     * cũ hoặc mới để tìm ra tên cột gốc.
     */
    private String extractColumnNameFromGlobalId(GlobalId globalId, DbmlModel beforeModel, DbmlModel currentModel) {
        // SỬA LỖI: Dùng toString() thay vì getPath()
        String fullIdString = globalId.toString();

        // Regex để tìm index của bảng và cột
        Pattern pattern = Pattern.compile("tables/(\\d+)/columns/(\\d+)");
        Matcher matcher = pattern.matcher(fullIdString);

        if (matcher.find()) {
            int tableIndex = Integer.parseInt(matcher.group(1));
            int columnIndex = Integer.parseInt(matcher.group(2));

            // Ưu tiên lấy tên cột từ model CŨ (beforeModel) vì nó đại diện cho trạng thái
            // của đối tượng "trước khi" thay đổi.
            if (tableIndex < beforeModel.getTables().size() &&
                    columnIndex < beforeModel.getTables().get(tableIndex).getColumns().size()) {
                return beforeModel.getTables().get(tableIndex).getColumns().get(columnIndex).getName();
            }

            // Nếu không có trong model cũ (ví dụ: cột mới được thêm), lấy từ model MỚI
            if (tableIndex < currentModel.getTables().size() &&
                    columnIndex < currentModel.getTables().get(tableIndex).getColumns().size()) {
                return currentModel.getTables().get(tableIndex).getColumns().get(columnIndex).getName();
            }
        }
        return "unknown_column"; // Trả về giá trị mặc định nếu không thể xác định
    }
    
    /**
     * Extracts the table name from a global ID.
     */
    private String extractTableName(String globalId) {
        String[] parts = globalId.split("/");
        return parts.length > 0 ? parts[parts.length - 1].replace("'", "") : "unknown";
    }

    private List<VersionComparisonDTO.TableDiff> changeAnalysis(String diffChanges) throws JsonProcessingException {
        List<VersionComparisonDTO.TableDiff> result = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(diffChanges);

        // Lấy danh sách các ID bảng từ các node JSON, chuyển thành Set để xử lý hiệu quả
        Set<String> addedTableIds = getIdsFromNode(rootNode.get("addedTables"));
        Set<String> removedTableIds = getIdsFromNode(rootNode.get("removedTables"));
        JsonNode tableChangesNode = rootNode.get("tableChanges");

        // Tập hợp tất cả các ID bảng từ mọi nguồn để không bỏ sót
        Set<String> allTableIds = new HashSet<>();
        allTableIds.addAll(addedTableIds);
        allTableIds.addAll(removedTableIds);
        if (tableChangesNode != null) {
            tableChangesNode.fieldNames().forEachRemaining(allTableIds::add);
        }

        // Duyệt qua tất cả các bảng có liên quan
        for (String tableId : allTableIds) {
            boolean isAdded = addedTableIds.contains(tableId);
            boolean isRemoved = removedTableIds.contains(tableId);
            boolean hasDetails = tableChangesNode != null && tableChangesNode.has(tableId);

            VersionComparisonDTO.TableDiff.TableDiffBuilder tableBuilder = VersionComparisonDTO.TableDiff.builder().tableName(tableId);

            // Áp dụng logic mới để xác định DiffType
            if (isAdded && !isRemoved) {
                tableBuilder.diffType(VersionComparisonDTO.DiffType.ADDED);
                // Một bảng mới có thể có chi tiết cột nếu được cung cấp
                if (hasDetails) {
                    tableBuilder.columnDiffs(processColumnChanges(tableChangesNode.get(tableId)));
                }
            } else if (isRemoved && !isAdded) {
                tableBuilder.diffType(VersionComparisonDTO.DiffType.REMOVED);
                // Bảng đã xóa không có chi tiết thay đổi cột
            } else if (hasDetails || (isAdded && isRemoved)) {
                // Trường hợp này chắc chắn là MODIFIED
                tableBuilder.diffType(VersionComparisonDTO.DiffType.MODIFIED);
                if (hasDetails) {
                    tableBuilder.columnDiffs(processColumnChanges(tableChangesNode.get(tableId)));
                }
            } else {
                // Bỏ qua nếu không rơi vào trường hợp nào (dữ liệu không nhất quán)
                continue;
            }

            result.add(tableBuilder.build());
        }

        return result;
    }
    // Helper để chuyển JsonNode chứa mảng string thành Set<String>
    private static Set<String> getIdsFromNode(JsonNode node) {
        if (node == null || !node.isArray()) {
            return Collections.emptySet();
        }
        return StreamSupport.stream(node.spliterator(), false)
                .map(JsonNode::asText)
                .collect(Collectors.toSet());
    }


    private static List<VersionComparisonDTO.ColumnDiff> processColumnChanges(JsonNode changesArray) {
        Map<String, VersionComparisonDTO.ColumnDiff.ColumnDiffBuilder> columnBuilders = new LinkedHashMap<>();
        List<JsonNode> unmappedTypeChanges = new ArrayList<>();

        for (JsonNode change : changesArray) {
            String property = change.get("property").asText();
            String newValue = change.get("newValue").asText(null);
            JsonNode oldValueNode = change.get("oldValue");
            boolean isAdded = oldValueNode == null || oldValueNode.isNull();

            if (property.equals("name")) {
                VersionComparisonDTO.ColumnDiff.ColumnDiffBuilder builder = columnBuilders.computeIfAbsent(newValue, k -> VersionComparisonDTO.ColumnDiff.builder());
                builder.columnName(newValue);

                if (isAdded) {
                    builder.diffType(VersionComparisonDTO.DiffType.ADDED);
                } else {
                    builder.diffType(VersionComparisonDTO.DiffType.MODIFIED);
                    builder.changedProperties(new ArrayList<>(Collections.singletonList("name")));
                }
            } else if (property.equals("dataType")) {
                if (isAdded) {
                    columnBuilders.values().stream()
                            .filter(b -> b.build().getDiffType() == VersionComparisonDTO.DiffType.ADDED && b.build().getCurrentType() == null)
                            .findFirst()
                            .ifPresent(builder -> builder.currentType(newValue));
                } else {
                    unmappedTypeChanges.add(change);
                }
            }
        }

        for(JsonNode typeChange : unmappedTypeChanges) {
            columnBuilders.values().stream()
                    .filter(b -> b.build().getDiffType() == VersionComparisonDTO.DiffType.MODIFIED && b.build().getBeforeType() == null)
                    .findFirst()
                    .ifPresent(builder -> {
                        builder.beforeType(typeChange.get("oldValue").asText());
                        builder.currentType(typeChange.get("newValue").asText());
                        List<String> props = new ArrayList<>(builder.build().getChangedProperties());
                        props.add("dataType");
                        builder.changedProperties(props);
                    });
        }

        return columnBuilders.values().stream().map(VersionComparisonDTO.ColumnDiff.ColumnDiffBuilder::build).collect(Collectors.toList());
    }

    public List<VersionComparisonDTO.TableDiff> createTableDiffs(Map<String, Object> diffResult, ObjectMapper objectMapper) {
        if (diffResult == null || diffResult.isEmpty()) {
            return Collections.emptyList();
        }

        List<VersionComparisonDTO.TableDiff> finalDiffs = new ArrayList<>();

        // 1. Xử lý các bảng được THÊM MỚI
        // Lấy về một danh sách các đối tượng TableModel, không phải chuỗi nữa
        List<TableModel> addedTableObjects = objectMapper.convertValue(
                diffResult.get("addedTables"),
                new TypeReference<List<TableModel>>() {}
        );

        if (addedTableObjects != null) {
            for (TableModel addedTable : addedTableObjects) {
                List<VersionComparisonDTO.ColumnDiff> addedColumns = new ArrayList<>();

                // Vì đã có sẵn đối tượng TableModel, ta có thể lấy danh sách cột trực tiếp
                if (addedTable.getColumns() != null) {
                    for (ColumnModel column : addedTable.getColumns()) {
                        addedColumns.add(VersionComparisonDTO.ColumnDiff.builder()
                                .columnName(column.getName())
                                .diffType(VersionComparisonDTO.DiffType.ADDED)
                                .currentType(column.getDataType())
                                .newValue(column)
                                .build());
                    }
                }

                finalDiffs.add(VersionComparisonDTO.TableDiff.builder()
                        .tableName(addedTable.getName())
                        .diffType(VersionComparisonDTO.DiffType.ADDED)
                        .columnDiffs(addedColumns) // <-- Danh sách cột bây giờ đã đầy đủ
                        .build());
            }
        }

        // 2. Xử lý các bảng đã BỊ XÓA
        List<String> removedTableNames = objectMapper.convertValue(diffResult.get("removedTables"), new TypeReference<>() {});
        if (removedTableNames != null) {
            for (String tableName : removedTableNames) {
                finalDiffs.add(VersionComparisonDTO.TableDiff.builder()
                        .tableName(tableName)
                        .diffType(VersionComparisonDTO.DiffType.REMOVED)
                        .build());
            }
        }

        // 3. Xử lý các bảng được SỬA ĐỔI
        Map<String, List<Map<String, Object>>> tableChanges = objectMapper.convertValue(
                diffResult.get("tableChanges"),
                new TypeReference<>() {}
        );

        if (tableChanges != null) {
            for (Map.Entry<String, List<Map<String, Object>>> entry : tableChanges.entrySet()) {
                String tableName = entry.getKey();
                List<VersionComparisonDTO.ColumnDiff> columnDiffs = new ArrayList<>();

                for (Map<String, Object> changeDetail : entry.getValue()) {
                    String changeTypeStr = (String) changeDetail.get("changeType");
                    VersionComparisonDTO.DiffType columnDiffType = VersionComparisonDTO.DiffType.valueOf(changeTypeStr); // ADDED, REMOVED, MODIFIED

                    VersionComparisonDTO.ColumnDiff.ColumnDiffBuilder columnBuilder = VersionComparisonDTO.ColumnDiff.builder().diffType(columnDiffType);

                    if (columnDiffType == VersionComparisonDTO.DiffType.ADDED) {
                        ColumnModel newColumn = objectMapper.convertValue(changeDetail.get("value"), ColumnModel.class);
                        columnBuilder.columnName(newColumn.getName())
                                .currentType(newColumn.getDataType())
                                .newValue(newColumn);
                    } else if (columnDiffType == VersionComparisonDTO.DiffType.REMOVED) {
                        ColumnModel oldColumn = objectMapper.convertValue(changeDetail.get("value"), ColumnModel.class);
                        columnBuilder.columnName(oldColumn.getName())
                                .beforeType(oldColumn.getDataType())
                                .oldValue(oldColumn);
                    } else if (columnDiffType == VersionComparisonDTO.DiffType.MODIFIED) {
                        ColumnModel oldColumn = objectMapper.convertValue(changeDetail.get("oldValue"), ColumnModel.class);
                        ColumnModel newColumn = objectMapper.convertValue(changeDetail.get("newValue"), ColumnModel.class);
                        columnBuilder.columnName(newColumn.getName()) // Lấy tên mới làm tên chính
                                .beforeType(oldColumn.getDataType())
                                .currentType(newColumn.getDataType())
                                .oldValue(oldColumn)
                                .newValue(newColumn);
                    }
                    columnDiffs.add(columnBuilder.build());
                }

                finalDiffs.add(VersionComparisonDTO.TableDiff.builder()
                        .tableName(tableName)
                        .diffType(VersionComparisonDTO.DiffType.MODIFIED)
                        .columnDiffs(columnDiffs)
                        .build());
            }
        }

        return finalDiffs;
    }


} 