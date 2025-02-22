package com.example.crm_backend.services;

import com.example.crm_backend.dtos.TaskDTO;
import com.example.crm_backend.entities.task.Task;
import com.example.crm_backend.entities.task.TaskValidator;
import com.example.crm_backend.entities.user.User;
import com.example.crm_backend.repositories.AccountRepository;
import com.example.crm_backend.repositories.TaskRepository;
import com.example.crm_backend.repositories.UserRepository;
import com.example.crm_backend.utils.ObjectMapper;
import com.example.crm_backend.utils.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

@Service
public class TaskService {
    private final TaskRepository task_repository;

    private final UserRepository user_repository;

    private final AccountRepository account_repository;

    @Autowired
    public TaskService(TaskRepository task_repository, UserRepository user_repository, AccountRepository account_repository) {
        this.task_repository = task_repository;
        this.user_repository = user_repository;
        this.account_repository = account_repository;
    }

    public Page<Task> getTaskByAccount(Long account_id, int ipp, int page, String query, Long manager_id, Long participant_id, Long status) {
        Pageable request = PageRequest.of(page, ipp, Sort.by(Sort.Direction.DESC, "id"));
        return task_repository.searchTasks(account_id, query, manager_id, participant_id, status, request);
    }

    public boolean isExistUser(Long id) {
        return user_repository.existsById(id);
    }

    public boolean isExistAccount(Long id) {
        return account_repository.existsById(id);
    }

    public Task createTask(TaskDTO task_dto, User user) {
        Task task = new Task();
        ObjectMapper.mapAll(task_dto, task);
        task.setStatus((long) Task.DRAFT);

        TaskValidator validator = new TaskValidator(task, this);
        validator.validate();

        task.setCreatorId(user.getId());
        task.setCreatedAt(Timer.now());
        task.setLastUpdate(Timer.now());

        return task_repository.save(task);
    }

    public Task getTask(Long id) {
        return task_repository.findById(id).orElse(null);
    }

    public Task duplicate(Long task_id, User user) {
        Task task = this.getTask(task_id);
        if (task == null) {
            throw new IllegalStateException("Invalid task. Please try again");
        }

        Task new_task = new Task();
        ObjectMapper.mapAll(task, new_task);
        new_task.setId(null);
        new_task.setStatus((long) Task.DRAFT);

        TaskValidator validator = new TaskValidator(new_task, this);
        validator.validate();

        new_task.setCreatorId(user.getId());
        new_task.setCreatedAt(Timer.now());
        new_task.setLastUpdate(Timer.now());

        return task_repository.save(new_task);
    }

    public Task editTask(Long task_id, TaskDTO dto, User user) {
        Task task = this.getTask(task_id);
        if (task == null) {
            throw new IllegalStateException("Invalid task. Please try again");
        }

        int status = Math.toIntExact(task.getStatus());
        if (status != Task.DRAFT && status != Task.IN_PROGRESS) {
            throw new IllegalStateException("You can not edit this task when the status is not draft or in progress");
        }

        task.setAttachment(dto.getAttachment());
        task.setNote(dto.getNote());
        task.setDescription(dto.getDescription());

        if (Objects.equals(user.getId(), task.getManagerId()) || Objects.equals(user.getId(), task.getCreatorId())) {
            task.setName(dto.getName());
            task.setProject(dto.getProject());
            task.setStartDate(dto.getStartDate());
            task.setEndDate(dto.getEndDate());
            task.setAccountId(dto.getAccountId());
            task.setParticipantId(dto.getParticipantId());
        }

        if (Objects.equals(user.getId(), task.getCreatorId())) {
            task.setManagerId(dto.getManagerId());
        }

        TaskValidator validator = new TaskValidator(task, this);
        validator.validate();
        task.setLastUpdate(Timer.now());

        return task_repository.save(task);
    }

    public void deleteTask(Long id) {
        task_repository.deleteById(id);
    }

    public Task start(Long task_id) {
        Task task = this.getTask(task_id);
        if (task == null) {
            throw new IllegalStateException("Invalid task. Please try again");
        }

        if (task.getStatus() != Task.DRAFT) {
            throw new IllegalStateException("You can not start this task when the status is not draft");
        }

        task.setStatus((long) Task.IN_PROGRESS);
        return task_repository.save(task);
    }

    public Task requestApproval(Long task_id) {
        Task task = this.getTask(task_id);
        if (task == null) {
            throw new IllegalStateException("Invalid task. Please try again");
        }

        if (task.getStatus() != Task.IN_PROGRESS) {
            throw new IllegalStateException("You can not request approval this task when the status is not in progress");
        }

        task.setStatus((long) Task.PENDING_APPROVAL);
        return task_repository.save(task);
    }

    public Task approve(Long task_id) {
        Task task = this.getTask(task_id);
        if (task == null) {
            throw new IllegalStateException("Invalid task. Please try again");
        }

        if (task.getStatus() != Task.PENDING_APPROVAL) {
            throw new IllegalStateException("You can not approve this task when the status is not pending approval");
        }

        task.setStatus((long) Task.APPROVED);

        return task_repository.save(task);
    }

    public Task reject(Long task_id) {
        Task task = this.getTask(task_id);
        if (task == null) {
            throw new IllegalStateException("Invalid task. Please try again");
        }

        if (task.getStatus() != Task.PENDING_APPROVAL) {
            throw new IllegalStateException("You can not reject this task when the status is not pending approval");
        }

        task.setStatus((long) Task.REJECTED);

        return task_repository.save(task);
    }

    public Task complete(Long task_id) {
        Task task = this.getTask(task_id);
        if (task == null) {
            throw new IllegalStateException("Invalid task. Please try again");
        }

        if (task.getStatus() != Task.APPROVED) {
            throw new IllegalStateException("You can not complete this task when this task is not approved by manager");
        }

        task.setStatus((long) Task.COMPLETED);

        return task_repository.save(task);
    }

    public Task cancel(Long task_id) {
        Task task = this.getTask(task_id);
        if (task == null) {
            throw new IllegalStateException("Invalid task. Please try again");
        }

        task.setStatus((long) Task.CANCELED);

        return task_repository.save(task);
    }

    public Page<Task> getTaskList(int ipp, int page, String query, Long manager_id, Long participant_id, Long status) {
        Pageable request = PageRequest.of(page, ipp, Sort.by(Sort.Direction.DESC, "id"));
        return task_repository.searchTasks(query, manager_id, participant_id, status, request);
    }

    public Long getCompletedTask(User user) {
        return task_repository.countTasksByUserAndStatus(user.getId(), (long) Task.COMPLETED);
    }

    public Long getProgressTask(User currentUser) {
        return task_repository.countTasksByUserAndStatus(currentUser.getId(), (long) Task.IN_PROGRESS);
    }

    public Long getExpiredTask(User user) {
        return task_repository.countExpiredTasks(user.getId(), Timer.now());
    }

    public List<Task> getUpcomingTasks(User user) {
        return task_repository.findUpcomingTasks(user.getId(), Timer.now(), Timer.  endOfDay(Timer.addDuration(Timer.now(), 1, ChronoUnit.DAYS)));
    }
}
