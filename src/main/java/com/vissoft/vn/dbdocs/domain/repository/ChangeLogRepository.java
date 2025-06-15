package com.vissoft.vn.dbdocs.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vissoft.vn.dbdocs.domain.entity.ChangeLog;

@Repository
public interface ChangeLogRepository extends JpaRepository<ChangeLog, String> {
    List<ChangeLog> findByProjectIdOrderByCreatedDateDesc(String projectId);

    @Query("SELECT c FROM ChangeLog c WHERE c.projectId = :projectId AND c.codeChangeLog LIKE :versionPrefix% ORDER BY c.codeChangeLog DESC")
    List<ChangeLog> findChangeLogsByVersionPrefix(@Param("projectId") String projectId, @Param("versionPrefix") String versionPrefix, Pageable pageable);
    
    default Optional<ChangeLog> findLatestChangeLogByVersionPrefix(String projectId, String versionPrefix) {
        List<ChangeLog> changeLogs = findChangeLogsByVersionPrefix(projectId, versionPrefix, org.springframework.data.domain.PageRequest.of(0, 1));
        return changeLogs.isEmpty() ? Optional.empty() : Optional.of(changeLogs.get(0));
    }
    
    @Query("SELECT c FROM ChangeLog c WHERE c.projectId = :projectId ORDER BY c.createdDate DESC")
    List<ChangeLog> findTopByProjectIdOrderByCreatedDateDesc(@Param("projectId") String projectId, Pageable pageable);
    
    default Optional<ChangeLog> findLatestChangeLogByProjectId(String projectId) {
        List<ChangeLog> changeLogs = findTopByProjectIdOrderByCreatedDateDesc(projectId, org.springframework.data.domain.PageRequest.of(0, 1));
        return changeLogs.isEmpty() ? Optional.empty() : Optional.of(changeLogs.get(0));
    }
    
    Optional<ChangeLog> findByProjectIdAndCodeChangeLog(String projectId, String codeChangeLog);
} 