package com.example.crm_backend.repositories;

import com.example.crm_backend.entities.account.product.AccountProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountProductRepository extends JpaRepository<AccountProduct, Long> {
    @Query(value = "SELECT * FROM account_products " +
            "WHERE account_id = :account_id " +
            "AND (:query IS NULL OR :query = '' OR MATCH(name, category) AGAINST (:query IN NATURAL LANGUAGE MODE)) " +
            "AND (:start = 0 OR last_update >= :start) " +
            "AND (:end = 0 OR last_update <= :end) " +
            "ORDER BY id DESC",
           countQuery = "SELECT COUNT(*) FROM account_products " +
                   "WHERE account_id = :account_id " +
                   "AND (:query IS NULL OR :query = '' OR MATCH(name, category) AGAINST (:query IN NATURAL LANGUAGE MODE)) " +
                   "AND (:start = 0 OR last_update >= :start) " +
                   "AND (:end = 0 OR last_update <= :end) ",
           nativeQuery = true)
    Page<AccountProduct> searchProducts(@Param("account_id") String account_id, @Param("query") String query, @Param("start") Long start, @Param("end") Long end, Pageable pageable);

    @Query(value = "SELECT * FROM account_products " +
            "WHERE account_id = :account_id " +
            "AND (:query IS NULL OR :query = '' OR MATCH(name, category) AGAINST (:query IN NATURAL LANGUAGE MODE)) " +
            "AND (:start = 0 OR last_update >= :start) " +
            "AND (:end = 0 OR last_update <= :end) " +
            "AND system_id = :system_id " +
            "ORDER BY id DESC",
            countQuery = "SELECT COUNT(*) FROM account_products " +
                    "WHERE account_id = :account_id " +
                    "AND (:query IS NULL OR :query = '' OR MATCH(name, category) AGAINST (:query IN NATURAL LANGUAGE MODE)) " +
                    "AND (:start = 0 OR last_update >= :start) " +
                    "AND (:end = 0 OR last_update <= :end) " +
                    "AND system_id = :system_id ",
            nativeQuery = true)
    Page<AccountProduct> searchProducts(@Param("account_id") String account_id, @Param("query") String query, @Param("start") Long start, @Param("end") Long end, @Param("system_id") Long system_id, Pageable pageable);

    void deleteBySystemId(Long system_id);
}
