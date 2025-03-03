package com.example.crm_backend.entities.account;

import com.example.crm_backend.dtos.account.AccountDTO;
import com.example.crm_backend.entities.Exportable;
import com.example.crm_backend.entities.Releasable;
import com.example.crm_backend.entities.user.User;
import com.example.crm_backend.enums.Gender;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Entity
@Table(name = "accounts")
@Getter
@Setter
public class Account implements Releasable<AccountDTO>, Exportable {
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

    @Column(name = "assigned_user_id")
    private Long assignedUserId;

    private Long birthday;

    private String job;

    @Column(name = "source_id")
    private Long sourceId;

    @Column(name = "referrer_id")
    private Long referrerId;

    @Column(name = "relationship_id")
    private Long relationshipId;

    @Column(name = "creator_id")
    private Long creatorId;

    @Column(name = "created_at")
    private Long createdAt;

    @Column(name = "last_update")
    private Long lastUpdate;

    @Column(name = "system_id")
    private Long systemId;

    @Transient
    private AccountACL acl = null;

    public Account() {
    }

    public Account(Long id, String name, String phone, String code, Gender gender, String email, Long assignedUserId, Long birthday, String job, Long sourceId, Long referrerId, Long relationshipId, Long creatorId, Long createdAt, Long lastUpdate, Long systemId) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.code = code;
        this.gender = gender;
        this.email = email;
        this.assignedUserId = assignedUserId;
        this.birthday = birthday;
        this.job = job;
        this.sourceId = sourceId;
        this.referrerId = referrerId;
        this.relationshipId = relationshipId;
        this.creatorId = creatorId;
        this.createdAt = createdAt;
        this.lastUpdate = lastUpdate;
        this.systemId = systemId;
    }

    public Account(String name, String phone, String code, Gender gender, String email, Long assignedUserId, Long birthday, String job, Long sourceId, Long referrerId, Long relationshipId, Long creatorId, Long createdAt, Long lastUpdate, Long systemId) {
        this.name = name;
        this.phone = phone;
        this.code = code;
        this.gender = gender;
        this.email = email;
        this.assignedUserId = assignedUserId;
        this.birthday = birthday;
        this.job = job;
        this.sourceId = sourceId;
        this.referrerId = referrerId;
        this.relationshipId = relationshipId;
        this.creatorId = creatorId;
        this.createdAt = createdAt;
        this.lastUpdate = lastUpdate;
        this.systemId = systemId;
    }

    public void setGender(Gender gender) {
        if (gender == null) {
            this.gender = Gender.OTHER;
            return ;
        }
        this.gender = gender;
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
        AccountDTO account_DTO = new AccountDTO();
        account_DTO.setId(id).setName(name).setGender(gender != null ? gender : Gender.OTHER).setBirthday(birthday)
                .setPhone(phone).setEmail(email).setCode(code).setAssignedUserId(assignedUserId).setJob(job);
        if (session_user != null) {
            account_DTO.setACL(Map.of(
                    "view", this.acl().canView(session_user),
                    "edit", this.acl().canEdit(session_user),
                    "delete", this.acl().canDelete(session_user)
            ));
        }

        return account_DTO;
    }

    @Override
    public AccountDTO releaseCompact() {
        return releaseCompact(null);
    }

    @Override
    public AccountDTO release(User session_user) {
        AccountDTO account_DTO = new AccountDTO();
        account_DTO.setId(id).setName(name).setGender(gender != null ? gender : Gender.OTHER).setBirthday(birthday)
                .setPhone(phone).setEmail(email).setCode(code).setAssignedUserId(assignedUserId).setJob(job)
                .setSourceId(sourceId).setRelationshipId(relationshipId).setReferrerId(referrerId)
                .setCreatorId(creatorId).setCreatedAt(createdAt).setLastUpdate(lastUpdate);
        if (session_user != null) {
            account_DTO.setACL(Map.of(
                    "view", this.acl().canView(session_user),
                    "edit", this.acl().canEdit(session_user),
                    "delete", this.acl().canDelete(session_user)
            ));
        }

        return account_DTO;
    }

    @Override
    public Map<String, Object> export() {
        return Map.of(
                "id", getId(),
                "name", "account"
        );
    }
}
