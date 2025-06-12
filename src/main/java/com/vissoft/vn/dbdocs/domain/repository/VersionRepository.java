package com.vissoft.vn.dbdocs.domain.repository;

import com.vissoft.vn.dbdocs.domain.entity.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VersionRepository extends JpaRepository<Version, String> {
    
    List<Version> findByProjectIdOrderByCodeVersionDesc(String projectId);
    
    @Query("SELECT v FROM Version v WHERE v.projectId = :projectId ORDER BY v.codeVersion DESC")
    List<Version> findAllVersionsByProjectIdOrdered(@Param("projectId") String projectId);
    
    @Query(value = "SELECT * FROM dbdocs.version WHERE project_id = :projectId ORDER BY code_version DESC LIMIT 1", nativeQuery = true)
    Optional<Version> findLatestVersionByProjectId(@Param("projectId") String projectId);
    
    Optional<Version> findByProjectIdAndCodeVersion(String projectId, Integer codeVersion);
    
    List<Version> findByProjectIdAndCodeVersionLessThanOrderByCodeVersionDesc(String projectId, Integer codeVersion);
} 