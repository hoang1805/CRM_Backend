package com.example.crm_backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class FeedbackDTO {
    private Long id;

    @JsonProperty("object_type")
    private String objectType;

    @JsonProperty("object_id")
    private Long objectId;

    private String content;

    @JsonProperty("rating")
    private Long rating;

    @JsonProperty("created_at")
    private Long createdAt;

    @JsonProperty("last_update")
    private Long lastUpdate;

    public FeedbackDTO() {
    }

    public FeedbackDTO(Long id, String objectType, Long objectId, String content, Long rating, Long createdAt, Long lastUpdate) {
        this.id = id;
        this.objectType = objectType;
        this.objectId = objectId;
        this.content = content;
        this.rating = rating;
        this.createdAt = createdAt;
        this.lastUpdate = lastUpdate;
    }
}
