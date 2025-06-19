package com.vissoft.vn.dbdocs.domain.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vissoft.vn.dbdocs.application.dto.VersionComparisonDTO;
import com.vissoft.vn.dbdocs.domain.entity.ChangeLog;
import com.vissoft.vn.dbdocs.domain.entity.Version;
import com.vissoft.vn.dbdocs.domain.exception.CustomException;
import com.vissoft.vn.dbdocs.domain.model.DbmlParser;
import com.vissoft.vn.dbdocs.domain.model.dbml.DbmlModel;
import com.vissoft.vn.dbdocs.domain.repository.ChangeLogRepository;
import com.vissoft.vn.dbdocs.domain.repository.VersionRepository;
import com.vissoft.vn.dbdocs.domain.service.VersionComparisonService;
import com.vissoft.vn.dbdocs.infrastructure.util.DataUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javers.core.Javers;
import org.javers.core.diff.Diff;
import org.javers.core.diff.Change;
import org.javers.core.diff.changetype.ValueChange;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
            String diffJson = objectMapper.writeValueAsString(diffChanges);
            
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
                    .build();
        } catch (Exception e) {
            log.error("Error parsing DBML or comparing versions", e);
            throw new CustomException("Error parsing DBML or comparing versions: " + e.getMessage(), 
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    private Map compareModels(DbmlModel beforeModel, DbmlModel currentModel) {
        log.debug("Comparing database models using Javers core");
        
        try {
            // Use Javers to compare the two models
            Diff diff = javers.compare(beforeModel, currentModel);
            
            // Create a root JSON object to hold the comparison results
            ObjectNode rootNode = objectMapper.createObjectNode();
            
            // Include basic metadata about the comparison
            rootNode.put("totalChanges", diff.getChanges().size());
            
            // Change statistics
            List<Change> newObjects = diff.getChanges().stream()
                    .filter(ValueChange.class::isInstance)
                    .toList();
            
            List<Change> removedObjects = diff.getChanges().stream()
                    .filter(ValueChange.class::isInstance)
                    .toList();
            
            List<Change> valueChanges = diff.getChanges().stream()
                    .filter(ValueChange.class::isInstance)
                    .toList();
            
            List<Change> listChanges = diff.getChanges().stream()
                    .filter(ValueChange.class::isInstance)
                    .toList();
            
            rootNode.put("newObjectsCount", newObjects.size());
            rootNode.put("removedObjectsCount", removedObjects.size());
            rootNode.put("valueChangesCount", valueChanges.size());
            rootNode.put("listChangesCount", listChanges.size());

            Map<String, ArrayNode> tableChanges = new HashMap<>();
            
            // Handle the new tables added
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
            
            // Analyze value changes and categorize them by table
            for (Change change : valueChanges) {
                if (change instanceof ValueChange valueChange) {
                    String tableName = extractTableName(valueChange.getAffectedGlobalId().toString());
                    
                    // Add the change to the corresponding table's change array
                    ArrayNode changesArray = tableChanges.computeIfAbsent(tableName, 
                            k -> objectMapper.createArrayNode());
                    
                    ObjectNode changeNode = objectMapper.createObjectNode();
                    changeNode.put("property", valueChange.getPropertyName());
                    changeNode.put("oldValue", DataUtils.notNull(valueChange.getLeft()) ? valueChange.getLeft().toString() : null);
                    changeNode.put("newValue", DataUtils.notNull(valueChange.getRight()) ? valueChange.getRight().toString() : null);
                    
                    changesArray.add(changeNode);
                }
            }
            
            // Add the added and removed tables to the root node
            rootNode.set("addedTables", addedTables);
            rootNode.set("removedTables", removedTables);
            
            // Add the table changes to the root node
            ObjectNode tableDetailsNode = objectMapper.createObjectNode();
            tableChanges.forEach(tableDetailsNode::set);
            rootNode.set("tableChanges", tableDetailsNode);
            
            // Change the structure of the diff output to a Map
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
     * Extracts the table name from a global ID.
     */
    private String extractTableName(String globalId) {
        String[] parts = globalId.split("/");
        return parts.length > 0 ? parts[parts.length - 1].replace("'", "") : "unknown";
    }
} 