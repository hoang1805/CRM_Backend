package com.example.crm_backend.dtos;

import com.example.crm_backend.dtos.account.AccountDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
public class TaskDTO {
    @JsonProperty("id")
    private Long id;

    private String name;

    private String description;

    private String note;

    private String project;

    private String attachment;

    @JsonProperty("start_date")
    private Long startDate;

    @JsonProperty("end_date")
    private Long endDate;

    @JsonProperty("status")
    private Long status;

    @JsonProperty("manager_id")
    private Long managerId;

    @JsonProperty("participant_id")
    private Long participantId;

    @JsonProperty("account_id")
    private Long accountId;

    @JsonProperty("creator_id")
    private Long creatorId;

    @JsonProperty("created_at")
    private Long createdAt;

    @JsonProperty("last_update")
    private Long lastUpdate;

    @JsonProperty("system_id")
    private Long systemId;

    @JsonProperty("account_export")
    private AccountDTO accountExport = null;

    private Map<String, Boolean> acl = new HashMap<>();

    public TaskDTO() {

    }

    public TaskDTO(Long id, String name, String description, String note, String project, String attachment, Long startDate, Long endDate, Long status, Long managerId, Long participantId, Long accountId, Long creatorId, Long createdAt, Long lastUpdate, Long systemId) {
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

}
