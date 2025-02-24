package com.example.crm_backend.repositories;

import com.example.crm_backend.entities.source.Source;
import com.example.crm_backend.entities.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SourceRepository extends JpaRepository<Source, Long> {
    boolean existsByCode(String code);

    @Query(value = "SELECT * FROM sources \n" +
            "               WHERE (:query IS NULL OR :query = '' OR MATCH(name, code) AGAINST (:query IN NATURAL LANGUAGE MODE)) \n" +
            "               ORDER BY id",
            nativeQuery = true)
    List<Source> searchSources(@Param("query") String query);
}
