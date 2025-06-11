package com.vissoft.vn.dbdocs.infrastructure.persistence.repository;

import com.vissoft.vn.dbdocs.infrastructure.persistence.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findBySocialId(String socialId);
} 