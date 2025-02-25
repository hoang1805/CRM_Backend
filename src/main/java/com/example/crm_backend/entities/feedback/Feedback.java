package com.example.crm_backend.entities.feedback;

import com.example.crm_backend.dtos.FeedbackDTO;
import com.example.crm_backend.entities.Releasable;
import com.example.crm_backend.entities.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "feedbacks")
@Getter
@Setter
public class Feedback implements Releasable<FeedbackDTO> {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @Column(name = "object_type")
    private String objectType;

    @Column(name = "object_id")
    private Long objectId;

    private String content;

    private Long rating;

    @Column(name = "created_at")
    private Long createdAt;

    @Column(name = "last_update")
    private Long lastUpdate;

    public Feedback() {}

    public Feedback(Long id, String objectType, Long objectId, String content, Long rating, Long createdAt, Long lastUpdate) {
        this.id = id;
        this.objectType = objectType;
        this.objectId = objectId;
        this.content = content;
        this.rating = rating;
        this.createdAt = createdAt;
        this.lastUpdate = lastUpdate;
    }

    public Feedback(String objectType, Long objectId, String content, Long rating, Long createdAt, Long lastUpdate) {
        this.objectType = objectType;
        this.objectId = objectId;
        this.content = content;
        this.rating = rating;
        this.createdAt = createdAt;
        this.lastUpdate = lastUpdate;
    }

    @Override
    public FeedbackDTO release(User session_user) {
        FeedbackDTO dto = new FeedbackDTO();
        dto.setId(id).setObjectType(objectType).setObjectId(objectId).setContent(content)
                .setRating(rating).setCreatedAt(createdAt)
                .setLastUpdate(lastUpdate);
        return dto;
    }

    @Override
    public FeedbackDTO release() {
        return release(null);
    }

    @Override
    public FeedbackDTO releaseCompact(User session_user) {
        return null;
    }

    @Override
    public FeedbackDTO releaseCompact() {
        return null;
    }
}
