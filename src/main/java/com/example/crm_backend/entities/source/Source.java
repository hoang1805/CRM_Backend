package com.example.crm_backend.entities.source;

import com.example.crm_backend.dtos.SourceDTO;
import com.example.crm_backend.entities.Releasable;
import com.example.crm_backend.entities.user.User;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Objects;

@Entity
@Table(name = "sources")
@Getter
@Setter
public class Source implements Releasable<SourceDTO> {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    private String name;

    private String code;

    @Nullable
    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "creator_id")
    private Long creatorId;

    @Column(name = "created_at")
    private Long createdAt;

    @Column(name = "last_update")
    private Long lastUpdate;

    @Column(name = "system_id")
    private Long systemId;

    @Transient
    private SourceACL acl = null;

    public Source() {
    }

    public Source(Long id, String name, String code, @Nullable Long parentId, Long creatorId, Long createdAt, Long lastUpdate, Long systemId) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.parentId = parentId;
        this.creatorId = creatorId;
        this.createdAt = createdAt;
        this.lastUpdate = lastUpdate;
        this.systemId = systemId;
    }

    public Source(String name, String code, @Nullable Long parentId, Long creatorId, Long createdAt, Long lastUpdate, Long systemId) {
        this.name = name;
        this.code = code;
        this.parentId = parentId;
        this.creatorId = creatorId;
        this.createdAt = createdAt;
        this.lastUpdate = lastUpdate;
        this.systemId = systemId;
    }

    public void setParentId(Long parent_id) {
        if (this.id != null && Objects.equals(this.id, parent_id)) {
            return ;
        }
        this.parentId = parent_id;
    }

    public SourceACL acl() {
        if (this.acl == null) {
            this.acl = new SourceACL(this);
        }
        return this.acl;
    }

    @Override
    public SourceDTO release(User session_user) {
        SourceDTO source_DTO = new SourceDTO();
        source_DTO.setId(id).setName(name).setCode(code).setParentId(parentId)
                .setCreatorId(creatorId).setCreatedAt(createdAt).setLastUpdate(lastUpdate);
        if (session_user != null) {
            source_DTO.setACL(Map.of(
                    "view", this.acl().canView(session_user),
                    "edit", this.acl().canEdit(session_user),
                    "delete", this.acl().canDelete(session_user)
            ));
        }

        return source_DTO;
    }

    @Override
    public SourceDTO release() {
        return release(null);
    }

    @Override
    public SourceDTO releaseCompact(User session_user) {
        return release(session_user);
    }

    @Override
    public SourceDTO releaseCompact() {
        return releaseCompact(null);
    }
}
