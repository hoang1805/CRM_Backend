package com.example.crm_backend.dtos;

import com.example.crm_backend.utils.converter.LongListConverter;
import com.example.crm_backend.utils.converter.MapConverter;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
public class RemindDTO {
    private Long id;

    private String message;

    private Boolean reminded;

    private Boolean enabled;

    @JsonProperty("remind_time")
    private Long remindTime;

    private Map<String, String> additional;

    @JsonProperty("user_ids")
    private List<Long> userIds;

    private String url;

    @JsonProperty("created_at")
    private Long createdAt;

    @JsonProperty("last_update")
    private Long lastUpdate;

    @JsonProperty("system_id")
    private Long systemId;

    public RemindDTO() {
    }

    public RemindDTO(Long id, String message, boolean reminded, boolean enabled, Long remindTime, Map<String, String> additional, List<Long> userIds, String url, Long createdAt, Long lastUpdate, Long systemId) {
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
}
