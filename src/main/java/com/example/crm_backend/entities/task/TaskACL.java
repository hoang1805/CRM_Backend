package com.example.crm_backend.entities.task;

import com.example.crm_backend.entities.user.User;
import com.example.crm_backend.enums.Role;

import java.util.Objects;

public class TaskACL {
    private final Task task;

    public TaskACL(Task task) {
        this.task = task;
    }

    public boolean canView(User user) {
        Long id = user.getId();
        return Objects.equals(user.getRole(), Role.ADMIN)
                || Objects.equals(id, task.getCreatorId())
                || Objects.equals(id, task.getManagerId())
                || Objects.equals(id, task.getParticipantId());
    }

    public boolean canEdit(User user) {
        Long id = user.getId();
        return Objects.equals(id, task.getCreatorId())
                || Objects.equals(id, task.getManagerId())
                || Objects.equals(id, task.getParticipantId());
    }

    public boolean canDelete(User user) {
        Long id = user.getId();
        return Objects.equals(id, task.getCreatorId())
                || Objects.equals(id, task.getManagerId());
    }

    public boolean canReview(User user) {
        return Objects.equals(user.getId(), task.getManagerId());
    }
}
