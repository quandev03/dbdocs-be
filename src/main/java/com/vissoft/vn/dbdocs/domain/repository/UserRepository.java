package com.vissoft.vn.dbdocs.domain.repository;

import com.vissoft.vn.dbdocs.domain.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, String> {
    Optional<Users> findBySocialId(String socialId);

    @Query(value = """
            select * from dbdocs.users u
            where u.email = :email
    """, nativeQuery = true)
    Optional<Users> findByEmail(@Param("email") String email);
} 