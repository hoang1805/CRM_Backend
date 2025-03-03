package com.example.crm_backend.entities.system;

import com.example.crm_backend.dtos.SystemDTO;
import com.example.crm_backend.entities.Releasable;
import com.example.crm_backend.entities.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Entity
@Table(name = "systems")
@Getter
@Setter
public class System implements Releasable<SystemDTO> {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    private String name;

    @Column(name = "max_user")
    private Long maxUser;

    @Column(name = "created_at")
    private Long createdAt;

    @Column(name = "last_update")
    private Long lastUpdate;

    @Transient
    private SystemACL acl = null;

    public System() {
    }

    public System(Long id, String name, Long maxUser, Long createdAt, Long lastUpdate) {
        this.id = id;
        this.name = name;
        this.maxUser = maxUser;
        this.createdAt = createdAt;
        this.lastUpdate = lastUpdate;
    }

    public System(String name, Long maxUser, Long createdAt, Long lastUpdate) {
        this.name = name;
        this.maxUser = maxUser;
        this.createdAt = createdAt;
        this.lastUpdate = lastUpdate;
    }

    public SystemACL acl() {
        if (this.acl == null) {
            this.acl = new SystemACL(this);
        }

        return this.acl;
    }

    @Override
    public SystemDTO release(User session_user) {
        SystemDTO dto = new SystemDTO();
        dto.setId(this.id).setName(this.name).setMaxUser(this.maxUser).setCreatedAt(this.createdAt).setLastUpdate(this.lastUpdate);
        if (session_user != null) {
            dto.setAcl(Map.of(
                    "view", this.acl().canView(session_user),
                    "edit", this.acl().canEdit(session_user),
                    "delete", this.acl().canDelete(session_user)
            ));
        }

        return dto;
    }

    @Override
    public SystemDTO release() {
        return release(null);
    }

    @Override
    public SystemDTO releaseCompact(User session_user) {
        return release(session_user);
    }

    @Override
    public SystemDTO releaseCompact() {
        return releaseCompact(null);
    }
}
