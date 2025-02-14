package com.example.crm_backend.entities.user;

import com.example.crm_backend.dtos.UserDTO;
import com.example.crm_backend.entities.Releasable;
import com.example.crm_backend.enums.Gender;
import com.example.crm_backend.enums.Role;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "users")
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

    private Long creator_id;

    private Long created_at;

    private Long last_update;

    @Transient
    private UserACL acl = null;

    public User() {
    }

    public User(Long id, String username, String name, String phone, String email, Gender gender, Long birthday, String title, Role role, String sign, String password, Long creator_id) {
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
        this.creator_id = creator_id;
    }

    public User(String username, String name, String phone, String email, Gender gender, Long birthday, String title, Role role, String sign, String password, Long creator_id) {
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
        this.creator_id = creator_id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getBirthday() {
        return birthday;
    }

    public void setBirthday(Long birthday) {
        this.birthday = birthday;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    public UserACL acl(){
        if (this.acl == null) {
            this.acl = new UserACL(this);
        }

        return this.acl;
    }

    @Override
    public UserDTO release(){
        return release(null);
    }

    @Override
    public UserDTO release(User session_user) {
        UserDTO user_DTO = new UserDTO();
        user_DTO.setId(id).setUsername(username).setName(name).setGender(gender != null ? gender : Gender.OTHER)
                .setBirthday(birthday)
                .setRole(role).setPhone(phone).setSign(sign).setEmail(email).setTitle(title)
                .setCreatorId(creator_id).setCreatedAt(created_at).setLastUpdate(last_update);
        if (session_user != null) {
            user_DTO.setACL(Map.of(
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
                .setRole(role).setTitle(title);
//        if (session_user != null) {
//            user_DTO.setACL(Map.of(
//                    "view", this.acl().canView(session_user),
//                    "edit", this.acl().canEdit(session_user),
//                    "delete", this.acl().canDelete(session_user)
//            ));
//        }

        return user_DTO;
    }

    @Override
    public UserDTO releaseCompact(){
        return releaseCompact(null);
    }
}
