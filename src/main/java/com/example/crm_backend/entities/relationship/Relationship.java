package com.example.crm_backend.entities.relationship;

import com.example.crm_backend.dtos.RelationshipDTO;
import com.example.crm_backend.entities.Releasable;
import com.example.crm_backend.entities.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Entity
@Table(name = "relationships")
@Getter
@Setter
public class Relationship implements Releasable<RelationshipDTO> {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    private String name;

    private String color;

    private String description;

    @Column(name = "creator_id")
    private Long creatorId;

    @Column(name = "create_at")
    private Long createdAt;

    @Column(name = "last_update")
    private Long lastUpdate;

    @Column(name = "system_id")
    private Long systemId;

    @Transient
    private RelationshipACL acl = null;

    public Relationship() {
    }

    public Relationship(Long id, String name, String color, String description, Long creatorId, Long createdAt, Long lastUpdate, Long systemId) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.description = description;
        this.creatorId = creatorId;
        this.createdAt = createdAt;
        this.lastUpdate = lastUpdate;
        this.systemId = systemId;
    }

    public Relationship(String name, String color, String description, Long creatorId, Long createdAt, Long lastUpdate, Long systemId) {
        this.name = name;
        this.color = color;
        this.description = description;
        this.creatorId = creatorId;
        this.createdAt = createdAt;
        this.lastUpdate = lastUpdate;
        this.systemId = systemId;
    }

    public RelationshipACL acl() {
        if (this.acl == null) {
            this.acl = new RelationshipACL(this);
        }

        return this.acl;
    }

    @Override
    public RelationshipDTO release(User session_user) {
        RelationshipDTO relationship_DTO = new RelationshipDTO();
        relationship_DTO.setId(id).setName(name).setColor(color).setDescription(description)
                .setCreatorId(creatorId).setCreatedAt(createdAt).setLastUpdate(lastUpdate);
        if (session_user != null) {
            relationship_DTO.setAcl(Map.of(
                    "view", this.acl().canView(session_user),
                    "edit", this.acl().canEdit(session_user),
                    "delete", this.acl().canDelete(session_user)
            ));
        }
        return relationship_DTO;
    }

    @Override
    public RelationshipDTO release() {
        return release(null);
    }

    @Override
    public RelationshipDTO releaseCompact(User session_user) {
        return release(session_user);
    }

    @Override
    public RelationshipDTO releaseCompact() {
        return releaseCompact(null);
    }

    @Override
    public String toString() {
        return "Relationship{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", description='" + description + '\'' +
                ", creator_id=" + creatorId +
                ", created_at=" + createdAt +
                ", last_update=" + lastUpdate +
                '}';
    }
}
