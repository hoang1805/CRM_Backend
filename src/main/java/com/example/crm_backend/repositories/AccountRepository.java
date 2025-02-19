package com.example.crm_backend.repositories;

import com.example.crm_backend.entities.account.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long>, JpaSpecificationExecutor<Account
        > {
    Page<Account> findAll(Pageable pageable);

    @Query("SELECT a FROM Account a WHERE a.creator_id = :user_id OR a.assigned_user_id = :user_id")
    Page<Account> findAllByUser(@Param("user_id") Long user_id, Pageable pageable);

    @Query("SELECT a FROM Account a WHERE a.email = ?1")
    Optional<Account> findByEmail(String email);

    long count();

    boolean existsByCode(String code);

    @Query("SELECT COUNT(a) FROM Account a WHERE a.creator_id = ?1 OR a.assigned_user_id = ?1")
    long countByUser(Long user_id);



    @Query(value = "SELECT * FROM accounts WHERE (:search IS NULL OR :search = '' OR MATCH(code, name, phone) AGAINST (:search IN NATURAL LANGUAGE MODE)) AND (:relationship_id = 0 OR relationship_id = :relationship_id) ORDER BY id DESC",
            countQuery = "SELECT COUNT(*) FROM accounts WHERE (:search IS NULL OR :search = '' OR MATCH(code, name, phone) AGAINST (:search IN NATURAL LANGUAGE MODE)) AND (:relationship_id = 0 OR relationship_id = :relationship_id) ORDER BY id DESC",
            nativeQuery = true)
    Page<Account> searchAccounts(@Param("search") String search, @Param("relationship_id") Long relationship_id, Pageable pageable);

    @Query(value = "SELECT * FROM accounts WHERE (:search IS NULL OR :search = '' OR MATCH(code, name, phone) AGAINST (:search IN NATURAL LANGUAGE MODE)) LIMIT :limit",
            nativeQuery = true)
    List<Account> searchAccounts(@Param("search") String search, @Param("limit") Long limit);

}
