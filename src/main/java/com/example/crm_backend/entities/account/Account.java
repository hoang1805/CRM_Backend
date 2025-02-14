package com.example.crm_backend.entities.account;

import com.example.crm_backend.dtos.AccountDTO;
import com.example.crm_backend.entities.Releasable;
import com.example.crm_backend.entities.user.User;
import com.example.crm_backend.enums.Gender;
import jakarta.persistence.*;

import java.util.Map;

@Entity
@Table(name = "accounts")
public class Account implements Releasable<AccountDTO> {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    private String name;

    private String phone;

    private String code;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String email;

    private Long assigned_user_id;

    private Long birthday;

    private String job;

    private Long source_id;

    private Long referrer_id;

    private Long relationship_id;

    private Long creator_id;

    private Long created_at;

    private Long last_update;

    @Transient
    private AccountACL acl = null;

    public Account(Long id, String name, String phone, String code, Gender gender, String email, Long assigned_user_id, Long birthday, String job, Long source_id, Long referrer_id, Long relationship_id, Long creator_id, Long created_at, Long last_update) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.code = code;
        this.gender = gender;
        this.email = email;
        this.assigned_user_id = assigned_user_id;
        this.birthday = birthday;
        this.job = job;
        this.source_id = source_id;
        this.referrer_id = referrer_id;
        this.relationship_id = relationship_id;
        this.creator_id = creator_id;
        this.created_at = created_at;
        this.last_update = last_update;
    }

    public Account(String name, String phone, String code, Gender gender, String email, Long assigned_user_id, Long birthday, String job, Long source_id, Long referrer_id, Long relationship_id, Long creator_id, Long created_at, Long last_update) {
        this.name = name;
        this.phone = phone;
        this.code = code;
        this.gender = gender;
        this.email = email;
        this.assigned_user_id = assigned_user_id;
        this.birthday = birthday;
        this.job = job;
        this.source_id = source_id;
        this.referrer_id = referrer_id;
        this.relationship_id = relationship_id;
        this.creator_id = creator_id;
        this.created_at = created_at;
        this.last_update = last_update;
    }

    public Account() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReferrerId() {
        return referrer_id;
    }

    public void setReferrerId(Long referrer_id) {
        this.referrer_id = referrer_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getBirthday() {
        return birthday;
    }

    public void setBirthday(Long birthday) {
        this.birthday = birthday;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getAssignedUserId() {
        return assigned_user_id;
    }

    public void setAssignedUserId(Long assigned_user_id) {
        this.assigned_user_id = assigned_user_id;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public Long getSourceId() {
        return source_id;
    }

    public void setSourceId(Long source_id) {
        this.source_id = source_id;
    }

    public Long getRelationshipId() {
        return relationship_id;
    }

    public void setRelationshipId(Long relationship_id) {
        this.relationship_id = relationship_id;
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

    public AccountACL acl() {
        if (this.acl == null) {
            this.acl = new AccountACL(this);
        }

        return this.acl;
    }

    @Override
    public AccountDTO release(){
        return release(null);
    }

    @Override
    public AccountDTO releaseCompact(User session_user) {
        return release(session_user);
    }

    @Override
    public AccountDTO releaseCompact() {
        return releaseCompact(null);
    }

    @Override
    public AccountDTO release(User session_user) {
        AccountDTO account_DTO = new AccountDTO();
        account_DTO.setId(id).setName(name).setGender(gender != null ? gender : Gender.OTHER)
                .setPhone(phone).setEmail(email).setCode(code).setAssignedUserId(assigned_user_id).setJob(job)
                .setSourceId(source_id).setRelationshipId(relationship_id).setReferrerId(referrer_id)
                .setCreatorId(creator_id).setCreatedAt(created_at).setLastUpdate(last_update);
        if (session_user != null) {
            account_DTO.setACL(Map.of(
                    "view", this.acl().canView(session_user),
                    "edit", this.acl().canEdit(session_user),
                    "delete", this.acl().canDelete(session_user)
            ));
        }

        return account_DTO;
    }
}
