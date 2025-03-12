package com.example.crm_backend.repositories;

import com.example.crm_backend.entities.account.product.AccountProduct;
import com.example.crm_backend.entities.feedback.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    @Query(value = "SELECT * FROM feedbacks WHERE object_id = :id AND object_type = :type", nativeQuery = true)
    List<Feedback> getByObject(@Param("id") String object_id, @Param("type") String object_type);

    @Query(value = "SELECT * FROM feedbacks " +
            "WHERE object_id = :account_id " +
            "AND object_type = \"account\" " +
            "AND (:query IS NULL OR :query = '' OR MATCH(content) AGAINST (:query IN NATURAL LANGUAGE MODE)) " +
            "AND (:start = 0 OR created_at >= :start) " +
            "AND (:end = 0 OR created_at <= :end) " +
            "ORDER BY id DESC",
            countQuery = "SELECT COUNT(*) FROM feedbacks " +
                    "WHERE object_id = :account_id " +
                    "AND object_type = \"account\" " +
                    "AND (:query IS NULL OR :query = '' OR MATCH(content) AGAINST (:query IN NATURAL LANGUAGE MODE)) " +
                    "AND (:start = 0 OR created_at >= :start) " +
                    "AND (:end = 0 OR created_at <= :end) ",
            nativeQuery = true)
    Page<Feedback> searchFeedbackByAccount(@Param("account_id") String account_id, @Param("query") String query, @Param("start") Long start, @Param("end") Long end, Pageable pageable);

    void deleteBySystemId(Long system_id);

    @Query(value = "SELECT * FROM feedbacks WHERE object_id = :id AND object_type = \"account\" ORDER BY id DESC LIMIT 1", nativeQuery = true)
    Feedback getLastByAccount(@Param("id") Long account_id);

    @Query(value = "SELECT COUNT(*) FROM feedbacks WHERE object_id = :id AND object_type = \"account\" ", nativeQuery = true)
    Long countContact(@Param("id") Long account_id);
}
