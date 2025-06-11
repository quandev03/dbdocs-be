package com.vissoft.vn.dbdocs.domain.repository;

import com.vissoft.vn.dbdocs.domain.entity.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VersionRepository extends JpaRepository<Version, String> {
} 