package com.example.crm_backend.entities.task;

import com.example.crm_backend.dtos.TaskDTO;
import com.example.crm_backend.entities.HasLink;
import com.example.crm_backend.entities.Releasable;
import com.example.crm_backend.entities.remind.Remind;
import com.example.crm_backend.entities.user.User;
import com.example.crm_backend.enums.Process;
import com.example.crm_backend.utils.converter.MapConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Entity
@Table(name = "tasks")
@Getter
@Setter
public class Task implements Releasable<TaskDTO>, HasLink {

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

    @Transient
    public static final int FAILED = 60;

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    private String name;

    private String description;

    private String note;

    @Enumerated(EnumType.STRING)
    private Process process;

    private boolean expired;

    @Convert(converter = MapConverter.class)
    private Map<String, String> data;

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

    public Task(Long id, String name, String description, String note, Process process, boolean expired, Map<String, String> data, String project, String attachment, Long startDate, Long endDate, Long status, Long managerId, Long participantId, Long accountId, Long creatorId, Long createdAt, Long lastUpdate, Long systemId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.note = note;
        this.process = process;
        this.expired = expired;
        this.data = data;
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

    public List<Long> collectUsers() {
        return List.of(creatorId, managerId, participantId);
    }

    public void enableRemind() {
        data.put("enable_remind", String.valueOf(true));
    }

    public void disableRemind() {
        data.put("enable_remind", String.valueOf(false));
    }

    public void addDuration(Long duration) {
        if (duration == null) {
            duration = 0L;
        }
        data.put("duration", String.valueOf(duration));
    }

    public Long getDuration() {
        String value = String.valueOf(data.get("duration"));
        if (value == null || value.isEmpty()) {
            return 0L;
        }

        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            return 0L;
        }
    }

    public void addRemind(Remind remind) {
        data.put("remind_id", String.valueOf(remind.getId()));
    }

    public Long getRemind() {
        String value = String.valueOf(data.get("remind_id"));
        if (value == null || value.isEmpty()) {
            return null;
        }

        return Long.parseLong(value);
    }

    public void removeRemind() {
        data.remove("remind_id");
    }

    public boolean isRemind() {
        String value = String.valueOf(data.get("enable_remind"));
        return Boolean.parseBoolean(value);
    }

    @Override
    public TaskDTO release(User session_user) {
        TaskDTO dto = new TaskDTO();
        dto.setId(id).setName(name).setDescription(description).setNote(note)
                .setProcess(process).setExpired(expired).setData(data)
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

    @Override
    public String getLink() {
        if (accountId == null) {
            return "/tasks";
        }

        return "/account/" + accountId + "?tab=task";
    }
}
