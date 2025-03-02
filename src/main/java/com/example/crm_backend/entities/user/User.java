package com.example.crm_backend.entities.user;

import com.example.crm_backend.dtos.UserDTO;
import com.example.crm_backend.entities.Releasable;
import com.example.crm_backend.enums.Gender;
import com.example.crm_backend.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User implements Releasable<UserDTO> {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    private String username;

    private String name;

    private String phone;

    private String email;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Long birthday;

    private String title;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String sign;

    private String password;

    @Column(name = "creator_id")
    private Long creatorId;

    @Column(name = "created_at")
    private Long createdAt;

    @Column(name = "last_update")
    private Long lastUpdate;

    @Column(name = "system_id")
    private Long systemId;

    @Transient
    private UserACL acl = null;

    public User() {
    }

    public User(String username, String name, String phone, String email, Gender gender, Long birthday, String title, Role role, String sign, String password, Long creatorId, Long createdAt, Long lastUpdate, Long systemId) {
        this.username = username;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.gender = gender;
        this.birthday = birthday;
        this.title = title;
        this.role = role;
        this.sign = sign;
        this.password = password;
        this.creatorId = creatorId;
        this.createdAt = createdAt;
        this.lastUpdate = lastUpdate;
        this.systemId = systemId;
    }

    public User(Long id, String username, String name, String phone, String email, Gender gender, Long birthday, String title, Role role, String sign, String password, Long creatorId, Long createdAt, Long lastUpdate, Long systemId) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.gender = gender;
        this.birthday = birthday;
        this.title = title;
        this.role = role;
        this.sign = sign;
        this.password = password;
        this.creatorId = creatorId;
        this.createdAt = createdAt;
        this.lastUpdate = lastUpdate;
        this.systemId = systemId;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    public UserACL acl() {
        if (this.acl == null) {
            this.acl = new UserACL(this);
        }

        return this.acl;
    }

    @Override
    public UserDTO release() {
        return release(null);
    }

    @Override
    public UserDTO release(User session_user) {
        UserDTO user_DTO = new UserDTO();
        user_DTO.setId(id).setUsername(username).setName(name).setGender(gender != null ? gender : Gender.OTHER)
                .setBirthday(birthday)
                .setRole(role).setPhone(phone).setSign(sign).setEmail(email).setTitle(title)
                .setCreatorId(creatorId).setCreatedAt(createdAt).setLastUpdate(lastUpdate).setSystemId(systemId);
        if (session_user != null) {
            user_DTO.setAcl(Map.of(
                    "view", this.acl().canView(session_user),
                    "edit", this.acl().canEdit(session_user),
                    "delete", this.acl().canDelete(session_user)
            ));
        }

        return user_DTO;
    }

    @Override
    public UserDTO releaseCompact(User session_user) {
        UserDTO user_DTO = new UserDTO();
        user_DTO.setId(id).setName(name).setUsername(username).setEmail(email).setPhone(phone)
                .setRole(role).setTitle(title).setSystemId(systemId);
        if (session_user != null) {
            user_DTO.setAcl(Map.of(
                    "view", this.acl().canView(session_user),
                    "edit", this.acl().canEdit(session_user),
                    "delete", this.acl().canDelete(session_user)
            ));
        }

        return user_DTO;
    }

    @Override
    public UserDTO releaseCompact() {
        return releaseCompact(null);
    }
}
