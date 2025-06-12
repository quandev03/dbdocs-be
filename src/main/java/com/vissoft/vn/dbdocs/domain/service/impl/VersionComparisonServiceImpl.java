package com.vissoft.vn.dbdocs.domain.service.impl;

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
import com.vissoft.vn.dbdocs.domain.model.dbml.DbmlModel;
import com.vissoft.vn.dbdocs.domain.model.dbml.TableModel;
import com.vissoft.vn.dbdocs.domain.repository.ChangeLogRepository;
import com.vissoft.vn.dbdocs.domain.repository.VersionRepository;
import com.vissoft.vn.dbdocs.domain.service.VersionComparisonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
import org.javers.core.diff.Change;
import org.javers.core.diff.changetype.NewObject;
import org.javers.core.diff.changetype.ObjectRemoved;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.diff.changetype.container.ListChange;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
        
        // Kiểm tra xem fromVersion có nhỏ hơn toVersion không
        if (toVersion != null && fromVersion >= toVersion) {
            throw new CustomException("From version must be less than to version", HttpStatus.BAD_REQUEST);
        }
        
        // Tìm phiên bản từ repository
        Optional<Version> fromVersionEntity = versionRepository.findByProjectIdAndCodeVersion(projectId, fromVersion);
        
        Optional<Version> toVersionEntity;
        if (toVersion != null) {
            // Nếu toVersion được chỉ định, tìm phiên bản đó
            toVersionEntity = versionRepository.findByProjectIdAndCodeVersion(projectId, toVersion);
        } else {
            // Nếu toVersion là null, sử dụng changelog mới nhất
            log.info("toVersion is null, using the latest changelog for comparison");
            toVersionEntity = Optional.empty(); // Không cần tìm Version entity, vì sẽ sử dụng changelog mới nhất
        }
        
        if (fromVersionEntity.isEmpty() || (toVersion != null && toVersionEntity.isEmpty())) {
            throw new CustomException("One or both versions not found", HttpStatus.NOT_FOUND);
        }
        
        // Lấy changelog tương ứng
        String fromChangeLogId = fromVersionEntity.get().getChangeLogId();
        Optional<ChangeLog> fromChangeLog = changeLogRepository.findById(fromChangeLogId);
        
        Optional<ChangeLog> toChangeLog;
        if (toVersion != null) {
            // Nếu toVersion được chỉ định, lấy changelog tương ứng
            String toChangeLogId = toVersionEntity.get().getChangeLogId();
            toChangeLog = changeLogRepository.findById(toChangeLogId);
        } else {
            // Nếu toVersion là null, lấy changelog mới nhất của project
            toChangeLog = changeLogRepository.findLatestChangeLogByProjectId(projectId);
        }
        
        if (fromChangeLog.isEmpty() || toChangeLog.isEmpty()) {
            throw new CustomException("One or both changelogs not found", HttpStatus.NOT_FOUND);
        }
        
        // Lấy nội dung DBML
        String fromDbml = fromChangeLog.get().getContent();
        String toDbml = toChangeLog.get().getContent();
        
        // Parse DBML thành đối tượng DbmlModel
        DbmlParser parser = new DbmlParser();
        
        try {
            DbmlModel beforeModel = parser.parse(fromDbml);
            DbmlModel currentModel = parser.parse(toDbml);
            
            // Xử lý so sánh và trả về kết quả
            Map<String, Object> diffChanges = compareModels(beforeModel, currentModel);
            String diffJson = objectMapper.writeValueAsString(diffChanges);
            
            // Xác định số phiên bản toVersion
            Integer actualToVersion = toVersion;
            if (toVersion == null) {
                // Nếu toVersion là null, sử dụng số phiên bản dự kiến (fromVersion + 1)
                actualToVersion = fromVersion + 1;
            }
            
            // Save diff to the toVersion entity if it exists
            if (toVersion != null) {
                toVersionEntity.get().setDiffChange(diffJson);
                versionRepository.save(toVersionEntity.get());
            }
            
            return VersionComparisonDTO.builder()
                    .projectId(projectId)
                    .fromVersion(fromVersion)
                    .toVersion(actualToVersion)
                    .diffChanges(diffJson)
                    .build();
        } catch (Exception e) {
            log.error("Error parsing DBML or comparing versions", e);
            throw new CustomException("Error parsing DBML or comparing versions: " + e.getMessage(), 
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    private Map<String, Object> compareModels(DbmlModel beforeModel, DbmlModel currentModel) throws JsonProcessingException {
        log.debug("Comparing database models using Javers core");
        
        try {
            // Sử dụng Javers chỉ để phân tích sự khác biệt, không lưu vào DB
            Diff diff = javers.compare(beforeModel, currentModel);
            
            // Tạo cấu trúc JSON phù hợp từ kết quả phân tích của Javers
            ObjectNode rootNode = objectMapper.createObjectNode();
            
            // Thông tin tổng quan
            rootNode.put("totalChanges", diff.getChanges().size());
            
            // Phân loại các thay đổi
            List<Change> newObjects = diff.getChanges().stream()
                    .filter(change -> change instanceof NewObject)
                    .collect(Collectors.toList());
            
            List<Change> removedObjects = diff.getChanges().stream()
                    .filter(change -> change instanceof ObjectRemoved)
                    .collect(Collectors.toList());
            
            List<Change> valueChanges = diff.getChanges().stream()
                    .filter(change -> change instanceof ValueChange)
                    .collect(Collectors.toList());
            
            List<Change> listChanges = diff.getChanges().stream()
                    .filter(change -> change instanceof ListChange)
                    .collect(Collectors.toList());
            
            rootNode.put("newObjectsCount", newObjects.size());
            rootNode.put("removedObjectsCount", removedObjects.size());
            rootNode.put("valueChangesCount", valueChanges.size());
            rootNode.put("listChangesCount", listChanges.size());
            
            // Thêm chi tiết về các bảng
            ObjectNode tablesNode = objectMapper.createObjectNode();
            
            // Phân loại thay đổi theo bảng
            Map<String, ArrayNode> tableChanges = new HashMap<>();
            
            // Xử lý các bảng mới
            ArrayNode addedTables = objectMapper.createArrayNode();
            for (Change change : newObjects) {
                if (change.getAffectedGlobalId().getTypeName().contains("TableModel")) {
                    String tableName = extractTableName(change.getAffectedGlobalId().toString());
                    addedTables.add(tableName);
                }
            }
            
            // Xử lý các bảng bị xóa
            ArrayNode removedTables = objectMapper.createArrayNode();
            for (Change change : removedObjects) {
                if (change.getAffectedGlobalId().getTypeName().contains("TableModel")) {
                    String tableName = extractTableName(change.getAffectedGlobalId().toString());
                    removedTables.add(tableName);
                }
            }
            
            // Phân tích các thay đổi về giá trị
            for (Change change : valueChanges) {
                if (change instanceof ValueChange) {
                    ValueChange valueChange = (ValueChange) change;
                    String tableName = extractTableName(valueChange.getAffectedGlobalId().toString());
                    
                    // Thêm thay đổi vào bảng tương ứng
                    ArrayNode changesArray = tableChanges.computeIfAbsent(tableName, 
                            k -> objectMapper.createArrayNode());
                    
                    ObjectNode changeNode = objectMapper.createObjectNode();
                    changeNode.put("property", valueChange.getPropertyName());
                    changeNode.put("oldValue", valueChange.getLeft() != null ? valueChange.getLeft().toString() : null);
                    changeNode.put("newValue", valueChange.getRight() != null ? valueChange.getRight().toString() : null);
                    
                    changesArray.add(changeNode);
                }
            }
            
            // Thêm tất cả thay đổi vào JSON
            rootNode.set("addedTables", addedTables);
            rootNode.set("removedTables", removedTables);
            
            // Thêm các thay đổi theo bảng
            ObjectNode tableDetailsNode = objectMapper.createObjectNode();
            tableChanges.forEach(tableDetailsNode::set);
            rootNode.set("tableChanges", tableDetailsNode);
            
            // Chuyển đổi sang Map để trả về
            return objectMapper.convertValue(rootNode, Map.class);
            
        } catch (Exception e) {
            log.error("Error using Javers for comparison: {}", e.getMessage(), e);
            
            // Fallback nếu Javers gặp lỗi
            Map<String, Object> fallback = new HashMap<>();
            fallback.put("error", "Failed to compare models: " + e.getMessage());
            fallback.put("tablesBeforeCount", beforeModel.getTables().size());
            fallback.put("tablesAfterCount", currentModel.getTables().size());
            fallback.put("tablesDiff", currentModel.getTables().size() - beforeModel.getTables().size());
            
            return fallback;
        }
    }
    
    /**
     * Trích xuất tên bảng từ ID của Javers
     */
    private String extractTableName(String globalId) {
        // Đơn giản hóa: chỉ lấy phần cuối của ID
        String[] parts = globalId.split("/");
        return parts.length > 0 ? parts[parts.length - 1].replace("'", "") : "unknown";
    }
} 