package com.example.crm_backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
public class NotificationDTO {
    private Long id;

    @JsonProperty("target_id")
    private Long targetId;

    @JsonProperty("source_id")
    private Long sourceId;

    private String message;

    @JsonProperty("is_read")
    private boolean isRead;

    private Map<String, String> additional;

    @JsonProperty("created_at")
    private Long createdAt;

    @JsonProperty("last_update")
    private Long lastUpdate;

    @JsonProperty("system_id")
    private Long systemId;

    public NotificationDTO() {
    }
}
