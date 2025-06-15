package com.vissoft.vn.dbdocs.domain.repository;

import com.vissoft.vn.dbdocs.domain.entity.ProjectAccess;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectAccessRepository extends JpaRepository<ProjectAccess, String> {
    List<ProjectAccess> findByProjectId(String projectId);
    
    Optional<ProjectAccess> findByProjectIdAndIdentifier(String projectId, String identifier);

    @Transactional
    void deleteByProjectIdAndIdentifier(String projectId, String identifier);
    
    /**
     * Tìm tất cả các project access của một user theo identifier
     */
    List<ProjectAccess> findByIdentifier(String identifier);
} 