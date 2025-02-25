package com.example.crm_backend.repositories;

import com.example.crm_backend.entities.task.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface
TaskRepository extends JpaRepository<Task, Long> {
    @Query(value = "SELECT * FROM tasks " +
            "WHERE account_id = :account_id " +
            "AND (:query IS NULL OR :query = '' OR MATCH(name, project) AGAINST (:query IN NATURAL LANGUAGE MODE)) " +
            "AND (:manager_id = 0 OR manager_id = :manager_id) " +
            "AND (:participant_id = 0 OR participant_id = :participant_id) " +
            "AND (:status = 0 OR status = :status) " +
            "ORDER BY id DESC",
            countQuery = "SELECT COUNT(*) FROM tasks " +
                    "WHERE account_id = :account_id " +
                    "AND (:query IS NULL OR :query = '' OR MATCH(name, project) AGAINST (:query IN NATURAL LANGUAGE MODE)) " +
                    "AND (:manager_id = 0 OR manager_id = :manager_id) " +
                    "AND (:participant_id = 0 OR participant_id = :participant_id) " +
                    "AND (:status = 0 OR status = :status) ",
            nativeQuery = true)
    Page<Task> searchTasks(
            @Param("account_id") Long account_id,
            @Param("query") String query,
            @Param("manager_id") Long managerId,
            @Param("participant_id") Long participantId,
            @Param("status") Long status,
            Pageable pageable);

    @Query(value = "SELECT * FROM tasks " +
            "WHERE (:query IS NULL OR :query = '' OR MATCH(name, project) AGAINST (:query IN NATURAL LANGUAGE MODE)) " +
            "AND (:manager_id = 0 OR manager_id = :manager_id) " +
            "AND (:participant_id = 0 OR participant_id = :participant_id) " +
            "AND (:status = 0 OR status = :status) " +
            "ORDER BY id DESC",
            countQuery = "SELECT COUNT(*) FROM tasks " +
                    "WHERE (:query IS NULL OR :query = '' OR MATCH(name, project) AGAINST (:query IN NATURAL LANGUAGE MODE)) " +
                    "AND (:manager_id = 0 OR manager_id = :manager_id) " +
                    "AND (:participant_id = 0 OR participant_id = :participant_id) " +
                    "AND (:status = 0 OR status = :status) ",
            nativeQuery = true)
    Page<Task> searchTasks(
            @Param("query") String query,
            @Param("manager_id") Long managerId,
            @Param("participant_id") Long participantId,
            @Param("status") Long status,
            Pageable pageable);

    @Query("SELECT COUNT(t) FROM Task t WHERE (t.participantId = :user OR t.managerId = :user) AND t.status = :status")
    Long countTasksByUserAndStatus(@Param("user") Long userId, @Param("status") Long status);

    @Query("SELECT COUNT(t) FROM Task t WHERE (t.participantId = :user OR t.managerId = :user) AND t.endDate < :expire AND t.status != 33 AND t.status < 40")
    Long countExpiredTasks(@Param("user") Long userId, @Param("expire") Long expire);

    @Query("SELECT t FROM Task t " +
            "WHERE (t.participantId = :user OR t.managerId = :user) " +
            "AND t.endDate BETWEEN :now AND :deadline " +
            "AND t.status != 33 AND t.status < 40 " +
            "ORDER BY t.endDate ASC")
    List<Task> findUpcomingTasks(@Param("user") Long userId,
                                 @Param("now") Long now,
                                 @Param("deadline") Long deadline);
}
