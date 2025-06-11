package com.vissoft.vn.dbdocs.domain.repository;

import com.vissoft.vn.dbdocs.domain.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, String> {
    Optional<Users> findBySocialId(String socialId);
} 