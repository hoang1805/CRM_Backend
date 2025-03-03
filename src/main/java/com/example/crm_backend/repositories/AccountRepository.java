package com.example.crm_backend.repositories;

import com.example.crm_backend.entities.account.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long>{
    boolean existsByCodeAndSystemId(String code, Long system_id);

    @Query(value = "SELECT * FROM accounts WHERE (:search IS NULL OR :search = '' OR MATCH(code, name, phone) AGAINST (:search IN NATURAL LANGUAGE MODE)) AND (:relationship_id = 0 OR relationship_id = :relationship_id) ORDER BY id DESC",
            countQuery = "SELECT COUNT(*) FROM accounts WHERE (:search IS NULL OR :search = '' OR MATCH(code, name, phone) AGAINST (:search IN NATURAL LANGUAGE MODE)) AND (:relationship_id = 0 OR relationship_id = :relationship_id) ORDER BY id DESC",
            nativeQuery = true)
    Page<Account> searchAccounts(@Param("search") String search, @Param("relationship_id") Long relationship_id, Pageable pageable);

    @Query(value = "SELECT * FROM accounts WHERE (:search IS NULL OR :search = '' OR MATCH(code, name, phone) AGAINST (:search IN NATURAL LANGUAGE MODE)) AND (:relationship_id = 0 OR relationship_id = :relationship_id) AND system_id = :system_id ORDER BY id DESC",
            countQuery = "SELECT COUNT(*) FROM accounts WHERE (:search IS NULL OR :search = '' OR MATCH(code, name, phone) AGAINST (:search IN NATURAL LANGUAGE MODE)) AND (:relationship_id = 0 OR relationship_id = :relationship_id) AND system_id = :system_id ORDER BY id DESC",
            nativeQuery = true)
    Page<Account> searchAccounts(@Param("search") String search, @Param("relationship_id") Long relationship_id, @Param("system_id") Long system_id,Pageable pageable);

    @Query(value = "SELECT * FROM accounts WHERE (:search IS NULL OR :search = '' OR MATCH(code, name, phone) AGAINST (:search IN NATURAL LANGUAGE MODE)) LIMIT :limit",
            nativeQuery = true)
    List<Account> searchAccounts(@Param("search") String search, @Param("limit") Long limit);

    @Query(value = "SELECT * FROM accounts WHERE (:search IS NULL OR :search = '' OR MATCH(code, name, phone) AGAINST (:search IN NATURAL LANGUAGE MODE)) AND system_id = :system_id LIMIT :limit",
            nativeQuery = true)
    List<Account> searchAccounts(@Param("search") String search, @Param("system_id") Long system_id, @Param("limit") Long limit);

    void deleteBySystemId(Long system_id);

    Long countBySystemId(Long systemId);
}
