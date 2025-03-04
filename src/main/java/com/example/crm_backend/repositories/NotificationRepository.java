package com.example.crm_backend.repositories;

import com.example.crm_backend.entities.notification.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByTargetIdAndSystemId(Long target_id, Long system_id);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.targetId = :userId AND n.systemId = :systemId")
    void markAllAsReadByUser(@Param("userId") Long userId, @Param("systemId") Long systemId);

    Page<Notification> findByTargetIdAndSystemId(Long target_id, Long system_id, Pageable request);

    @Query(value = "SELECT * FROM notifications " +
            "WHERE target_id = :target_id " +
            "AND system_id = :system_id " +
            "ORDER BY id DESC",
            countQuery = "SELECT COUNT(*) FROM notifications " +
                    "WHERE target_id = :target_id " +
                    "AND system_id = :system_id ",
            nativeQuery = true)
    Page<Notification> getNotifications(@Param("target_id") Long target_id, @Param("system_id") Long system_id, Pageable request);

    long countByTargetIdAndSystemId(Long target_id, Long system_id);

    long countByTargetIdAndSystemIdAndIsRead(Long target_id, Long system_id, boolean is_read);
}
