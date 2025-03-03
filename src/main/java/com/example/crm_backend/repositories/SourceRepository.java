package com.example.crm_backend.repositories;

import com.example.crm_backend.entities.source.Source;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SourceRepository extends JpaRepository<Source, Long> {
    boolean existsByCodeAndSystemId(String code, Long system_id);

    @Query(value = "SELECT * FROM sources \n" +
            "               WHERE (:query IS NULL OR :query = '' OR MATCH(name, code) AGAINST (:query IN NATURAL LANGUAGE MODE)) \n" +
            "               ORDER BY id",
            nativeQuery = true)
    List<Source> searchSources(@Param("query") String query);

    @Query(value = "SELECT * FROM sources \n" +
            "               WHERE (:query IS NULL OR :query = '' OR MATCH(name, code) AGAINST (:query IN NATURAL LANGUAGE MODE)) \n" +
            "               AND system_id = :system_id \n" +
            "               ORDER BY id",
            nativeQuery = true)
    List<Source> searchSources(@Param("query") String query, @Param("system_id") Long system_id);

    void deleteBySystemId(Long system_id);

    List<Source> findBySystemId(Long system_id, Sort sort);
}
