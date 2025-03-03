package com.example.crm_backend.entities.task;

import com.example.crm_backend.dtos.TaskDTO;
import com.example.crm_backend.entities.Releasable;
import com.example.crm_backend.entities.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Entity
@Table(name = "tasks")
@Getter
@Setter
public class Task implements Releasable<TaskDTO> {

    @Transient
    public static final int DRAFT = 10;

    @Transient
    public static final int IN_PROGRESS = 20;

    @Transient
    public static final int PENDING_APPROVAL = 30;

    @Transient
    public static final int REJECTED = 33;

    @Transient
    public static final int APPROVED = 36;

    @Transient
    public static final int COMPLETED = 40;

    @Transient
    public static final int CANCELED = 50;

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    private String name;

    private String description;

    private String note;

    private String project;

    private String attachment;

    @Column(name = "start_date")
    private Long startDate;

    @Column(name = "end_date")
    private Long endDate;

    private Long status;

    @Column(name = "manager_id")
    private Long managerId;

    @Column(name = "participant_id")
    private Long participantId;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "creator_id")
    private Long creatorId;

    @Column(name = "created_at")
    private Long createdAt;

    @Column(name = "last_update")
    private Long lastUpdate;

    @Column(name = "system_id")
    private Long systemId;

    @Transient
    private TaskACL acl;

    public Task() {
    }

    public Task(Long id, String name, String description, String note, String project, String attachment, Long startDate, Long endDate, Long status, Long managerId, Long participantId, Long accountId, Long creatorId, Long createdAt, Long lastUpdate, Long systemId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.note = note;
        this.project = project;
        this.attachment = attachment;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.managerId = managerId;
        this.participantId = participantId;
        this.accountId = accountId;
        this.creatorId = creatorId;
        this.createdAt = createdAt;
        this.lastUpdate = lastUpdate;
        this.systemId = systemId;
    }

    public Task(String name, String description, String note, String project, String attachment, Long startDate, Long endDate, Long status, Long managerId, Long participantId, Long accountId, Long creatorId, Long createdAt, Long lastUpdate, Long systemId) {
        this.name = name;
        this.description = description;
        this.note = note;
        this.project = project;
        this.attachment = attachment;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.managerId = managerId;
        this.participantId = participantId;
        this.accountId = accountId;
        this.creatorId = creatorId;
        this.createdAt = createdAt;
        this.lastUpdate = lastUpdate;
        this.systemId = systemId;
    }

    public TaskACL acl() {
        if (this.acl == null) {
            this.acl = new TaskACL(this);
        }
        return this.acl;
    }

    @Override
    public TaskDTO release(User session_user) {
        TaskDTO dto = new TaskDTO();
        dto.setId(id).setName(name).setDescription(description).setNote(note)
                .setProject(project).setAttachment(attachment).setStartDate(startDate)
                .setEndDate(endDate).setStatus(status).setManagerId(managerId)
                .setParticipantId(participantId).setAccountId(accountId)
                .setCreatorId(creatorId).setCreatedAt(createdAt).setLastUpdate(lastUpdate);

        if (session_user != null) {
            dto.setAcl(Map.of(
                    "view", this.acl().canView(session_user),
                    "edit", this.acl().canEdit(session_user),
                    "delete", this.acl().canDelete(session_user),
                    "review", this.acl().canReview(session_user)
            ));
        }

        return dto;
    }

    @Override
    public TaskDTO release() {
        return release(null);
    }

    @Override
    public TaskDTO releaseCompact(User session_user) {
        return null;
    }

    @Override
    public TaskDTO releaseCompact() {
        return null;
    }
}
