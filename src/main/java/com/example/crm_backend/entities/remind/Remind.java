package com.example.crm_backend.entities.remind;

import com.example.crm_backend.dtos.RemindDTO;
import com.example.crm_backend.entities.Releasable;
import com.example.crm_backend.entities.user.User;
import com.example.crm_backend.utils.Timer;
import com.example.crm_backend.utils.converter.LongListConverter;
import com.example.crm_backend.utils.converter.MapConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Entity
@Table(name = "reminds")
@Getter
@Setter
public class Remind implements Releasable<RemindDTO> {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    private String message;

    private boolean reminded;

    private boolean enabled;

    @Column(name = "remind_time")
    private Long remindTime;

    @Convert(converter = MapConverter.class)
    private Map<String, String> additional;

    @Convert(converter = LongListConverter.class)
    @Column(name = "user_ids")
    private List<Long> userIds;

    private String url;

    @Column(name = "created_at")
    private Long createdAt;

    @Column(name = "last_update")
    private Long lastUpdate;

    @Column(name = "system_id")
    private Long systemId;

    public Remind() {
    }

    public Remind(Long id, String message, boolean reminded, boolean enabled, Long remindTime, Map<String, String> additional, List<Long> userIds, String url, Long createdAt, Long lastUpdate, Long systemId) {
        this.id = id;
        this.message = message;
        this.reminded = reminded;
        this.enabled = enabled;
        this.remindTime = remindTime;
        this.additional = additional;
        this.userIds = userIds;
        this.url = url;
        this.createdAt = createdAt;
        this.lastUpdate = lastUpdate;
        this.systemId = systemId;
    }

    public Remind(String message, Long remindTime, Map<String, String> additional, List<Long> userIds, String url, Long systemId) {
        this.message = message;
        this.reminded = false;
        this.enabled = true;
        this.remindTime = remindTime;
        this.additional = additional;
        this.userIds = userIds;
        this.url = url;
        this.systemId = systemId;
        this.createdAt = Timer.now();
        this.lastUpdate = Timer.now();
    }

    @Override
    public RemindDTO release(User session_user) {
        RemindDTO dto = new RemindDTO();
        dto.setId(id).setMessage(message).setReminded(reminded).setEnabled(enabled)
                .setRemindTime(remindTime).setAdditional(additional)
                .setUserIds(userIds).setUrl(url).setCreatedAt(createdAt)
                .setLastUpdate(lastUpdate).setSystemId(systemId);
        return dto;
    }

    @Override
    public RemindDTO release() {
        return release(null);
    }

    @Override
    public RemindDTO releaseCompact(User session_user) {
        return null;
    }

    @Override
    public RemindDTO releaseCompact() {
        return null;
    }
}
