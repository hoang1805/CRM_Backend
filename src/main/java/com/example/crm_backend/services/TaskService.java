package com.example.crm_backend.services;

import com.example.crm_backend.dtos.TaskDTO;
import com.example.crm_backend.entities.remind.Remind;
import com.example.crm_backend.entities.task.Task;
import com.example.crm_backend.entities.task.TaskValidator;
import com.example.crm_backend.entities.user.User;
import com.example.crm_backend.enums.Process;
import com.example.crm_backend.enums.Role;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class TaskService {
    private final TaskRepository task_repository;

    private final UserRepository user_repository;

    private final AccountRepository account_repository;

    private final SystemService system_service;

    private final RemindService remind_service;

    private final NotificationService notification_service;

    @Autowired
    public TaskService(TaskRepository task_repository, UserRepository user_repository, AccountRepository account_repository, SystemService systemService, RemindService remindService, NotificationService notificationService) {
        this.task_repository = task_repository;
        this.user_repository = user_repository;
        this.account_repository = account_repository;
        system_service = systemService;
        remind_service = remindService;
        notification_service = notificationService;
    }

    public Page<Task> getTaskByAccount(User user, Long account_id, int ipp, int page, String query, Long manager_id, Long participant_id, Long status) {
        Pageable request = PageRequest.of(page, ipp, Sort.by(Sort.Direction.DESC, "id"));
        if (user.getRole() == Role.SUPER_ADMIN) {
            return task_repository.searchTasks(account_id, query, manager_id, participant_id, status, request);
        }

        return task_repository.searchTasks(account_id, query, manager_id, participant_id, status, user.getSystemId(), request);
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
        task.setData(new HashMap<>());

        TaskValidator validator = new TaskValidator(task, this);
        validator.validate();

        task.setEndDate(Timer.endOfDay(task.getEndDate()));

        calculateProcess(task);
        checkExpiredTask(task);

        task.setCreatorId(user.getId());
        task.setCreatedAt(Timer.now());
        task.setLastUpdate(Timer.now());
        task.setSystemId(user.getSystemId());

        return task_repository.save(task);
    }

    public Task getTask(Long id) {
        return task_repository.findById(id).orElse(null);
    }

    public Task getTask(Long id, Long system_id) {
        return task_repository.findByIdAndSystemId(id, system_id).orElse(null);
    }

    public Task duplicate(Long task_id, User user) {
        Task task = this.getTask(task_id, user.getSystemId());
        if (task == null) {
            throw new IllegalStateException("Invalid task. Please try again");
        }

        if (!system_service.existsById(task.getSystemId())) {
            throw new IllegalStateException("Invalid task. Please try again");
        }

        Task new_task = new Task();
        ObjectMapper.mapAll(task, new_task);
        new_task.setId(null);
        new_task.setStatus((long) Task.DRAFT);
        new_task.setProcess(Process.BEGIN);
        new_task.disableRemind();
        new_task.removeRemind();
        new_task.setExpired(false);

        TaskValidator validator = new TaskValidator(new_task, this);
        validator.validate();

        new_task.setCreatorId(user.getId());
        new_task.setCreatedAt(Timer.now());
        new_task.setLastUpdate(Timer.now());

        return task_repository.save(new_task);
    }

    public Task editTask(Long task_id, TaskDTO dto, User user) {
        Task task = this.getTask(task_id, user.getSystemId());
        if (task == null) {
            throw new IllegalStateException("Invalid task. Please try again");
        }

        if (!system_service.existsById(task.getSystemId())) {
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

        task.setEndDate(Timer.endOfDay(task.getEndDate()));
        this.calculateProcess(task);

        TaskValidator validator = new TaskValidator(task, this);
        validator.validate();

        if (task.isRemind()) {
            remind_service.edit(task.getRemind(), task.getEndDate() - task.getDuration());
        }

        checkExpiredTask(task);
        task.setLastUpdate(Timer.now());

        return task_repository.save(task);
    }

    public void deleteTask(Long id) {
        task_repository.deleteById(id);
    }

    public Task start(Long task_id, User user) {
        Task task = this.getTask(task_id, user.getSystemId());
        if (task == null) {
            throw new IllegalStateException("Invalid task. Please try again");
        }

        if (!system_service.existsById(task.getSystemId())) {
            throw new IllegalStateException("Invalid task. Please try again");
        }

        if (task.getStatus() != Task.DRAFT) {
            throw new IllegalStateException("You can not start this task when the status is not draft");
        }

        task.setStatus((long) Task.IN_PROGRESS);
        this.calculateProcess(task);
        checkExpiredTask(task);
        return task_repository.save(task);
    }

    public Task requestApproval(Long task_id, User user) {
        Task task = this.getTask(task_id, user.getSystemId());
        if (task == null) {
            throw new IllegalStateException("Invalid task. Please try again");
        }

        if (!system_service.existsById(task.getSystemId())) {
            throw new IllegalStateException("Invalid task. Please try again");
        }

        if (task.getStatus() != Task.IN_PROGRESS) {
            throw new IllegalStateException("You can not request approval this task when the status is not in progress");
        }

        task.setStatus((long) Task.PENDING_APPROVAL);
        this.calculateProcess(task);
        checkExpiredTask(task);
        return task_repository.save(task);
    }

    public Task approve(Long task_id, User user) {
        Task task = this.getTask(task_id, user.getSystemId());
        if (task == null) {
            throw new IllegalStateException("Invalid task. Please try again");
        }

        if (!system_service.existsById(task.getSystemId())) {
            throw new IllegalStateException("Invalid task. Please try again");
        }

        if (task.getStatus() != Task.PENDING_APPROVAL) {
            throw new IllegalStateException("You can not approve this task when the status is not pending approval");
        }

        task.setStatus((long) Task.APPROVED);
        this.calculateProcess(task);
        checkExpiredTask(task);

        return task_repository.save(task);
    }

    public Task reject(Long task_id, User user) {
        Task task = this.getTask(task_id, user.getSystemId());
        if (task == null) {
            throw new IllegalStateException("Invalid task. Please try again");
        }

        if (!system_service.existsById(task.getSystemId())) {
            throw new IllegalStateException("Invalid task. Please try again");
        }

        if (task.getStatus() != Task.PENDING_APPROVAL) {
            throw new IllegalStateException("You can not reject this task when the status is not pending approval");
        }

        task.setStatus((long) Task.REJECTED);
        this.calculateProcess(task);
        checkExpiredTask(task);

        return task_repository.save(task);
    }

    public Task complete(Long task_id, User user) {
        Task task = this.getTask(task_id, user.getSystemId());
        if (task == null) {
            throw new IllegalStateException("Invalid task. Please try again");
        }

        if (!system_service.existsById(task.getSystemId())) {
            throw new IllegalStateException("Invalid task. Please try again");
        }

        if (task.getStatus() != Task.APPROVED) {
            throw new IllegalStateException("You can not complete this task when this task is not approved by manager");
        }

        task.setStatus((long) Task.COMPLETED);
        this.calculateProcess(task);
        checkExpiredTask(task);

        return task_repository.save(task);
    }

    public Task cancel(Long task_id, User user) {
        Task task = this.getTask(task_id, user.getSystemId());
        if (task == null) {
            throw new IllegalStateException("Invalid task. Please try again");
        }

        if (!system_service.existsById(task.getSystemId())) {
            throw new IllegalStateException("Invalid task. Please try again");
        }

        task.setStatus((long) Task.CANCELED);
        this.calculateProcess(task);
        checkExpiredTask(task);

        return task_repository.save(task);
    }

    public Page<Task> getTaskList(User user, int ipp, int page, String query, Long manager_id, Long participant_id, Long status) {
        Pageable request = PageRequest.of(page, ipp, Sort.by(Sort.Direction.DESC, "id"));
        if (user.getRole() == Role.SUPER_ADMIN) {
            return task_repository.searchTasks(query, manager_id, participant_id, status, request);
        }

        return task_repository.searchTasks(query, manager_id, participant_id, status, user.getSystemId(), request);
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
        return task_repository.findUpcomingTasks(user.getId(), Timer.now(), Timer.  endOfDay(Timer.addDuration(Timer.now(), 1, ChronoUnit.DAYS)), user.getSystemId());
    }

    public Task enableRemind(Long task_id, User user) {
        Task task = this.getTask(task_id, user.getSystemId());
        if (task == null) {
            throw new IllegalStateException("Invalid task. Please try again");
        }

        if (!system_service.existsById(task.getSystemId())) {
            throw new IllegalStateException("Invalid task. Please try again");
        }

        if (task.isRemind()) {
            throw new IllegalStateException("This task has already been enabled remind.");
        }

        long status = task.getStatus();

        if (status == Task.DRAFT) {
            throw new IllegalStateException("You can not create the reminder because the task is not started. Please try again");
        }

        if (status == Task.CANCELED || status == Task.REJECTED || status == Task.COMPLETED) {
            throw new IllegalStateException("You can not create the reminder because the task has been cancel, rejected or completed. Please try again");
        }

        if (task.getProcess() == Process.EXPIRED) {
            throw new IllegalStateException("You can not create the reminder because the task has been expired. Please try again");
        }

        Long duration = task.getDuration();
        Long remindTime = task.getEndDate() - duration;
        String message = "The task ${name} is about to expired. Please complete it quickly";
        Map<String, String> data = Map.of(
                "name", task.getName()
        );
        try {
            Remind remind = remind_service.create(message, remindTime, data, task.collectUsers(), task.getLink(), task.getSystemId());

            task.enableRemind();
            task.addDuration(duration);
            task.addRemind(remind);

            return task_repository.save(task);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    public Task editRemind(Long task_id, User user, Long duration) {
        Task task = this.getTask(task_id, user.getSystemId());

        if (task == null) {
            throw new IllegalStateException("Invalid task. Please try again");
        }

        if (!system_service.existsById(task.getSystemId())) {
            throw new IllegalStateException("Invalid task. Please try again");
        }

        if (!task.isRemind()) {
            throw new IllegalStateException("This task is not enabled remind.");
        }

        if (duration == null) {
            throw new IllegalStateException("Invalid duration. Please try again");
        }

        Long remind_id = task.getRemind();
        if (remind_id == null) {
            throw new IllegalStateException("Invalid reminder");
        }

        Remind remind = remind_service.edit(remind_id, task.getEndDate() - duration);
        task.addDuration(duration);

        return task_repository.save(task);
    }

    public Task disableRemind(Long task_id, User user) {
        Task task = this.getTask(task_id, user.getSystemId());
        if (task == null) {
            throw new IllegalStateException("Invalid task. Please try again");
        }

        if (!system_service.existsById(task.getSystemId())) {
            throw new IllegalStateException("Invalid task. Please try again");
        }

        try {
            disableRemind(task);
            return task_repository.save(task);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    private void disableRemind(Task task) {
        if (!task.isRemind()) {
            return;
        }

        Long remind_id = task.getRemind();
        remind_service.delete(remind_id);

        task.disableRemind();
        task.removeRemind();
    }

    public void checkExpiredTask(Task task) {
        if (task.getStatus() == Task.DRAFT) {
            task.setExpired(false);
            return;
        }

        if (task.getProcess() == Process.EXPIRED && !task.isExpired()) {
            task.setExpired(true);
            notification_service.systemNotify("Task", task.collectUsers(), "The task ${object_name} has been expired", task.getName(), task.getLink(), task.getSystemId());
            return ;
        }

        if (task.getProcess() == Process.DOING) {
            task.setExpired(false);
        }
    }

    public void calculateProcess(Task task) {
        long status = task.getStatus();
        if (status == Task.DRAFT) {
            task.setProcess(Process.BEGIN);
            return;
        }

        Process process = task.getProcess();

        if (process == Process.END) {
            disableRemind(task);
            return;
        }

        if (status == Task.IN_PROGRESS || status == Task.PENDING_APPROVAL || status == Task.APPROVED ) {
            if (task.getEndDate() < Timer.now()) {
                task.setProcess(Process.EXPIRED);
            } else {
                task.setProcess(Process.DOING);
            }
        }

        if (process == Process.EXPIRED) {
            disableRemind(task);
            return ;
        }

        if (status == Task.REJECTED) {
            task.setProcess(Process.END);
        }

        if (status == Task.CANCELED) {
            task.setProcess(Process.END);
        }

        if (status == Task.COMPLETED) {
            task.setProcess(Process.END);
        }
    }
}
