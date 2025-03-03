package com.example.crm_backend.entities.notification;

import com.example.crm_backend.dtos.NotificationDTO;
import com.example.crm_backend.entities.Releasable;
import com.example.crm_backend.entities.user.User;
import com.example.crm_backend.utils.Timer;
import com.example.crm_backend.utils.converter.MapConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "notifications")
@Getter
@Setter
public class Notification implements Releasable<NotificationDTO> {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @Column(name = "target_id")
    private Long targetId;

    @Column(name = "source_id")
    private Long sourceId;

    private String message;

    @Column(name = "is_read")
    private boolean isRead;

    @Convert(converter = MapConverter.class)
    private Map<String, String> additional;

    private String url;

    @Column(name = "created_at")
    private Long createdAt;

    @Column(name = "last_update")
    private Long lastUpdate;

    @Column(name = "system_id")
    private Long systemId;

    public Notification() {
    }

    public Notification(Long id, Long targetId, Long sourceId, String message, boolean isRead, Map<String, String> additional, String url, Long createdAt, Long lastUpdate, Long systemId) {
        this.id = id;
        this.targetId = targetId;
        this.sourceId = sourceId;
        this.message = message;
        this.isRead = isRead;
        this.additional = additional;
        this.url = url;
        this.createdAt = createdAt;
        this.lastUpdate = lastUpdate;
        this.systemId = systemId;
    }

    public Notification(Long targetId, Long sourceId, String message, Long systemId) {
        this.targetId = targetId;
        this.sourceId = sourceId;
        this.message = message;
        this.isRead = false;
        this.additional = new HashMap<>();
        this.url = "";
        this.createdAt = Timer.now();
        this.lastUpdate = Timer.now();
        this.systemId = systemId;
    }

    public Notification(Long targetId, Long sourceId, String message, Map<String, String> additional,Long systemId) {
        this.targetId = targetId;
        this.sourceId = sourceId;
        this.message = message;
        this.isRead = false;
        this.additional = additional;
        this.url = "";
        this.createdAt = Timer.now();
        this.lastUpdate = Timer.now();
        this.systemId = systemId;
    }

    public Notification(Long targetId, Long sourceId, String message, String url, Long systemId) {
        this.targetId = targetId;
        this.sourceId = sourceId;
        this.message = message;
        this.isRead = false;
        this.additional = new HashMap<>();
        this.url = url;
        this.createdAt = Timer.now();
        this.lastUpdate = Timer.now();
        this.systemId = systemId;
    }

    public Notification(Long targetId, Long sourceId, String message, Map<String, String> additional, String url, Long systemId) {
        this.targetId = targetId;
        this.sourceId = sourceId;
        this.message = message;
        this.isRead = false;
        this.additional = additional;
        this.url = url;
        this.createdAt = Timer.now();
        this.lastUpdate = Timer.now();
        this.systemId = systemId;
    }

    @Override
    public NotificationDTO release(User session_user) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(id).setAdditional(additional).setRead(isRead)
                .setTargetId(targetId).setSourceId(sourceId).setMessage(message).setSystemId(systemId);
        return dto;
    }

    @Override
    public NotificationDTO release() {
        return release(null);
    }

    @Override
    public NotificationDTO releaseCompact(User session_user) {
        return null;
    }

    @Override
    public NotificationDTO releaseCompact() {
        return null;
    }
}
