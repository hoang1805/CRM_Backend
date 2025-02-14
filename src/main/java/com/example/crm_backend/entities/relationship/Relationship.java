package com.example.crm_backend.entities.relationship;

import com.example.crm_backend.dtos.RelationshipDTO;
import com.example.crm_backend.entities.Releasable;
import com.example.crm_backend.entities.user.User;
import jakarta.persistence.*;

import java.util.Map;

@Entity
@Table(name = "relationships")
public class Relationship implements Releasable<RelationshipDTO> {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    private String name;

    private String color;

    private String description;

    private Long creator_id;

    private Long created_at;

    private Long last_update;

    @Transient
    private RelationshipACL acl = null;

    public Relationship() {
    }

    public Relationship(Long id, String name, String color, String description, Long creator_id, Long created_at, Long last_update) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.description = description;
        this.creator_id = creator_id;
        this.created_at = created_at;
        this.last_update = last_update;
    }

    public Relationship(String name, String color, String description, Long creator_id, Long created_at, Long last_update) {
        this.name = name;
        this.color = color;
        this.description = description;
        this.creator_id = creator_id;
        this.created_at = created_at;
        this.last_update = last_update;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCreatorId() {
        return creator_id;
    }

    public void setCreatorId(Long creator_id) {
        this.creator_id = creator_id;
    }

    public Long getCreatedAt() {
        return created_at;
    }

    public void setCreatedAt(Long created_at) {
        this.created_at = created_at;
    }

    public Long getLastUpdate() {
        return last_update;
    }

    public void setLastUpdate(Long last_update) {
        this.last_update = last_update;
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
                .setCreatorId(creator_id).setCreatedAt(created_at).setLastUpdate(last_update);
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
                ", creator_id=" + creator_id +
                ", created_at=" + created_at +
                ", last_update=" + last_update +
                '}';
    }
}
