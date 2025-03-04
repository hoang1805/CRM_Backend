package com.example.crm_backend.controllers;

import com.example.crm_backend.dtos.account.AccountDTO;
import com.example.crm_backend.dtos.TaskDTO;
import com.example.crm_backend.entities.account.Account;
import com.example.crm_backend.entities.task.Task;
import com.example.crm_backend.entities.user.User;
import com.example.crm_backend.enums.Role;
import com.example.crm_backend.services.NotificationService;
import com.example.crm_backend.services.account.AccountService;
import com.example.crm_backend.services.TaskService;
import com.example.crm_backend.services.UserService;
import com.example.crm_backend.utils.SessionHelper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping(path = "api/task")
public class TaskController {
    private final UserService user_service;

    private final TaskService task_service;

    private final AccountService account_service;

    private final NotificationService notification_service;

    @Autowired
    public TaskController(UserService user_service, TaskService task_service, AccountService accountService, NotificationService notificationService) {
        this.user_service = user_service;
        this.task_service = task_service;
        account_service = accountService;
        notification_service = notificationService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getTask(@PathVariable("id") Long id, HttpServletRequest request) {
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        Task task = task_service.getTask(id);
        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Invalid task"));
        }

        if (!task.acl().canView(current_user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("code", "FORBIDDEN", "message", "You do not have permission"));
        }

        return ResponseEntity.ok(task.release(current_user));
    }

    @GetMapping("/account/{id}")
    public ResponseEntity<Object> getTaskByAccount(@PathVariable("id") Long id, @RequestParam(defaultValue = "10") int ipp, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "") String query, @RequestParam(defaultValue = "0") Long manager_id, @RequestParam(defaultValue = "0") Long participant_id, @RequestParam(defaultValue = "0") Long status, HttpServletRequest request) {
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        if (!task_service.isExistAccount(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Invalid account"));
        }

        Page<Task> tasks = task_service.getTaskByAccount(current_user, id, ipp, page, query, manager_id, participant_id, status);
        Page<TaskDTO> data = tasks.map(task -> task.release(current_user));

        return ResponseEntity.ok(data);
    }

    @GetMapping("/list")
    public ResponseEntity<Object> getTasks(@RequestParam(defaultValue = "10") int ipp, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "") String query, @RequestParam(defaultValue = "0") Long manager_id, @RequestParam(defaultValue = "0") Long participant_id, @RequestParam(defaultValue = "0") Long status, HttpServletRequest request) {
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }
        
        Page<Task> tasks = task_service.getTaskList(current_user, ipp, page, query, manager_id, participant_id, status);
        Page<TaskDTO> data = tasks.map(task -> task.release(current_user));

        return ResponseEntity.ok(facet(data));
    }

    private Page<TaskDTO> facet(Page<TaskDTO> data) {
        List<Long> account_ids = data.stream().flatMap(taskDTO -> Stream.of(taskDTO.getAccountId())).filter(Objects::nonNull).toList();
        List<Account> accounts = account_service.loadAccounts(account_ids);
        Map<Long, AccountDTO> map = accounts.stream()
                .collect(Collectors.toMap(Account::getId, Account::releaseCompact));

        return data.map(task -> {
            task.setAccountExport(map.get(task.getAccountId()));
            return task;
        });
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createTask(@RequestBody TaskDTO task_DTO, HttpServletRequest request) {
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        if (current_user.getRole() != Role.ADMIN && current_user.getRole() != Role.MANAGER && current_user.getRole() != Role.STAFF) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("code", "FORBIDDEN", "message", "You do not have permission"));
        }

        try {
            Task task = task_service.createTask(task_DTO, current_user);
            notification_service.notify(current_user, "Task", List.of(task.getCreatorId(), task.getManagerId(), task.getParticipantId()), "${user} created a new Task ${object_name}", task.getName(), task.getLink());
            return ResponseEntity.ok(Map.of("task", task.release(current_user)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("code", "BAD_REQUEST", "message", e.getMessage()));
        }
    }

    @PostMapping("/edit/{id}")
    public ResponseEntity<Object> editTask(@PathVariable("id") Long id, @RequestBody TaskDTO task_DTO, HttpServletRequest request){
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        Task task = task_service.getTask(id);
        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Invalid task"));
        }

        if (!task.acl().canEdit(current_user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("code", "FORBIDDEN", "message", "You do not have permission"));
        }

        try {
            Task new_task = task_service.editTask(id, task_DTO, current_user);
            notification_service.notify(current_user, "Task", List.of(task.getCreatorId(), task.getManagerId(), task.getParticipantId()), "${user} edited a Task ${object_name} that you followed", task.getName(), task.getLink());
            return ResponseEntity.ok(Map.of("task", new_task.release(current_user)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("code", "BAD_REQUEST", "message", e.getMessage()));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteTask(@PathVariable("id") Long id, HttpServletRequest request) {
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        Task task = task_service.getTask(id);
        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Invalid task"));
        }

        if (!task.acl().canDelete(current_user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("code", "FORBIDDEN", "message", "You do not have permission"));
        }

        try {
            task_service.deleteTask(id);
            notification_service.notify(current_user, "Task", List.of(task.getCreatorId(), task.getManagerId(), task.getParticipantId()), "${user} deleted a Task ${object_name} that you followed", task.getName());
            return ResponseEntity.ok(Map.of("message", "Delete successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("code", "BAD_REQUEST", "message", e.getMessage()));
        }
    }

    @PostMapping("/duplicate/{id}")
    public ResponseEntity<Object> duplicateTask(@PathVariable("id") Long id, HttpServletRequest request) {
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        Task task = task_service.getTask(id);
        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Invalid task"));
        }

        if (!task.acl().canEdit(current_user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("code", "FORBIDDEN", "message", "You do not have permission"));
        }

        try {
            Task new_task = task_service.duplicate(id, current_user);
            notification_service.notify(current_user, "Task", List.of(new_task.getCreatorId(), new_task.getManagerId(), new_task.getParticipantId()), "${user} duplicated a Task ${object_name} that you followed", new_task.getName(), new_task.getLink());
            return ResponseEntity.ok(Map.of("task", new_task.release(current_user)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("code", "BAD_REQUEST", "message", e.getMessage()));
        }
    }

    @PostMapping("/start/{id}")
    public ResponseEntity<Object> startTask(@PathVariable("id") Long id, HttpServletRequest request) {
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        Task task = task_service.getTask(id);
        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Invalid task"));
        }

        if (!task.acl().canEdit(current_user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("code", "FORBIDDEN", "message", "You do not have permission"));
        }

        try {
            Task new_task = task_service.start(id, current_user);
            notification_service.notify(current_user, "Task", List.of(new_task.getCreatorId(), new_task.getManagerId(), new_task.getParticipantId()), "${user} started a Task ${object_name} that you followed", new_task.getName(), new_task.getLink());
            return ResponseEntity.ok(Map.of("task", new_task.release(current_user)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("code", "BAD_REQUEST", "message", e.getMessage()));
        }
    }

    @PostMapping("/request.approval/{id}")
    public ResponseEntity<Object> requestApproval(@PathVariable("id") Long id, HttpServletRequest request) {
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        Task task = task_service.getTask(id);
        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Invalid task"));
        }

        if (!task.acl().canEdit(current_user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("code", "FORBIDDEN", "message", "You do not have permission"));
        }

        try {
            Task new_task = task_service.requestApproval(id, current_user);
            notification_service.notify(current_user, "Task", List.of(new_task.getCreatorId(), new_task.getManagerId(), new_task.getParticipantId()), "${user} requested approval a Task ${object_name} that you followed", new_task.getName(), new_task.getLink());
            return ResponseEntity.ok(Map.of("task", new_task.release(current_user)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("code", "BAD_REQUEST", "message", e.getMessage()));
        }
    }

    @PostMapping("/approve/{id}")
    public ResponseEntity<Object> approve(@PathVariable("id") Long id, HttpServletRequest request) {
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        Task task = task_service.getTask(id);
        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Invalid task"));
        }

        if (!Objects.equals(current_user.getId(), task.getManagerId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("code", "FORBIDDEN", "message", "You do not have permission"));
        }

        try {
            Task new_task = task_service.approve(id, current_user);
            notification_service.notify(current_user, "Task", List.of(new_task.getCreatorId(), new_task.getManagerId(), new_task.getParticipantId()), "${user} approved a Task ${object_name} that you followed", new_task.getName(), new_task.getLink());
            return ResponseEntity.ok(Map.of("task", new_task.release(current_user)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("code", "BAD_REQUEST", "message", e.getMessage()));
        }
    }

    @PostMapping("/reject/{id}")
    public ResponseEntity<Object> reject(@PathVariable("id") Long id, HttpServletRequest request) {
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        Task task = task_service.getTask(id);
        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Invalid task"));
        }

        if (!Objects.equals(current_user.getId(), task.getManagerId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("code", "FORBIDDEN", "message", "You do not have permission"));
        }

        try {
            Task new_task = task_service.reject(id, current_user);
            notification_service.notify(current_user, "Task", List.of(new_task.getCreatorId(), new_task.getManagerId(), new_task.getParticipantId()), "${user} rejected a Task ${object_name} that you followed", new_task.getName(), new_task.getLink());
            return ResponseEntity.ok(Map.of("task", new_task.release(current_user)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("code", "BAD_REQUEST", "message", e.getMessage()));
        }
    }

    @PostMapping("/complete/{id}")
    public ResponseEntity<Object> complete(@PathVariable("id") Long id, HttpServletRequest request) {
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        Task task = task_service.getTask(id);
        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Invalid task"));
        }

        if (!Objects.equals(current_user.getId(), task.getManagerId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("code", "FORBIDDEN", "message", "You do not have permission"));
        }

        try {
            Task new_task = task_service.complete(id, current_user);
            notification_service.notify(current_user, "Task", List.of(new_task.getCreatorId(), new_task.getManagerId(), new_task.getParticipantId()), "${user} completed a Task ${object_name} that you followed", new_task.getName(), new_task.getLink());
            return ResponseEntity.ok(Map.of("task", new_task.release(current_user)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("code", "BAD_REQUEST", "message", e.getMessage()));
        }
    }

    @PostMapping("/cancel/{id}")
    public ResponseEntity<Object> cancel(@PathVariable("id") Long id, HttpServletRequest request) {
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        Task task = task_service.getTask(id);
        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Invalid task"));
        }

        if (!Objects.equals(current_user.getId(), task.getManagerId()) && !Objects.equals(current_user.getId(), task.getCreatorId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("code", "FORBIDDEN", "message", "You do not have permission"));
        }

        try {
            Task new_task = task_service.cancel(id, current_user);
            notification_service.notify(current_user, "Task", List.of(new_task.getCreatorId(), new_task.getManagerId(), new_task.getParticipantId()), "${user} canceled a Task ${object_name} that you followed", new_task.getName(), new_task.getLink());
            return ResponseEntity.ok(Map.of("task", new_task.release(current_user)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("code", "BAD_REQUEST", "message", e.getMessage()));
        }
    }
}
