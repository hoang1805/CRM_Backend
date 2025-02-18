package com.example.crm_backend.entities.task;

import com.example.crm_backend.services.TaskService;


public class TaskValidator {
    private final Task task;

    private final TaskService task_service;

    public TaskValidator(Task task, TaskService task_service) {
        this.task = task;
        this.task_service = task_service;
    }

    public TaskValidator validName() {
        if (task.getName() == null || task.getName().isEmpty()) {
            throw new IllegalStateException("Invalid task name. Please try again");
        }

        return this;
    }

    public TaskValidator validProject() {
        if (task.getProject() == null || task.getProject().isEmpty()) {
            throw new IllegalStateException("Invalid project name. Please try again");
        }

        return this;
    }

    public TaskValidator validManager() {
        if (task.getManagerId() == null) {
            throw new IllegalStateException("Invalid manager id. Please try again");
        }

        if (!task_service.isExistUser(task.getManagerId())) {
            throw new IllegalStateException("Manager does not exist. Please try again");
        }

        return this;
    }

    public TaskValidator validParticipant() {
        if (task.getParticipantId() == null) {
            throw new IllegalStateException("Invalid participant id. Please try again");
        }

        if (!task_service.isExistUser(task.getParticipantId())) {
            throw new IllegalStateException("Participant does not exist. Please try again");
        }

        return this;
    }

    public TaskValidator validAccount() {
        Long account_id = task.getAccountId();
        if (account_id != null && !task_service.isExistAccount(account_id)) {
            throw new IllegalStateException("Account does not exist. Please try again");
        }

        return this;
    }

    public TaskValidator validDate() {
        if (task.getStartDate() == null || task.getEndDate() == null) {
            throw new IllegalStateException("Invalid date. Please try again");
        }

        if (task.getStartDate() > task.getEndDate()) {
            throw new IllegalStateException("Invalid date. Please try again");
        }

        return this;
    }

    public TaskValidator validStatus() {
        Long status = task.getStatus();
        if (status == null) {
            throw new IllegalStateException("Invalid status. Please try again");
        }

        if (status != Task.DRAFT
                && status != Task.IN_PROGRESS
                && status != Task.PENDING_APPROVAL
                && status != Task.APPROVED
                && status != Task.REJECTED
                && status != Task.COMPLETED
                && status != Task.CANCELED
        ) {
            throw new IllegalStateException("Invalid status. Please try again");
        }

        return this;
    }

    public void validate() {
        validName().validDate().validProject().validStatus()
                .validManager().validParticipant().validAccount();
    }


}
