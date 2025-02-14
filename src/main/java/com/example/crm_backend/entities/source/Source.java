package com.example.crm_backend.entities.source;

import com.example.crm_backend.dtos.SourceDTO;
import com.example.crm_backend.entities.Releasable;
import com.example.crm_backend.entities.user.User;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;

import java.util.Map;
import java.util.Objects;

@Entity
@Table(name = "sources")
public class Source implements Releasable<SourceDTO> {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    private String name;

    private String code;

    @Nullable
    private Long parent_id;

    private Long creator_id;

    private Long created_at;

    private Long last_update;

    @Transient
    private SourceACL acl = null;

    public Source() {
    }

    public Source(Long id, String name, String code, Long parent_id, Long creator_id, Long created_at, Long last_update) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.parent_id = parent_id;
        this.creator_id = creator_id;
        this.created_at = created_at;
        this.last_update = last_update;
    }

    public Source(String name, String code, Long parent_id, Long creator_id, Long created_at, Long last_update) {
        this.name = name;
        this.code = code;
        this.parent_id = parent_id;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getParentId() {
        return parent_id;
    }

    public void setParentId(Long parent_id) {
        if (this.id != null && Objects.equals(this.id, parent_id)) {
            return ;
        }
        this.parent_id = parent_id;
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

    public SourceACL acl() {
        if (this.acl == null) {
            this.acl = new SourceACL(this);
        }
        return this.acl;
    }

    @Override
    public SourceDTO release(User session_user) {
        SourceDTO source_DTO = new SourceDTO();
        source_DTO.setId(id).setName(name).setCode(code).setParentId(parent_id)
                .setCreatorId(creator_id).setCreatedAt(created_at).setLastUpdate(last_update);
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
