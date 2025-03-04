package com.example.crm_backend.services.cronjob;

import com.example.crm_backend.entities.task.Task;
import com.example.crm_backend.repositories.TaskRepository;
import com.example.crm_backend.services.NotificationService;
import com.example.crm_backend.services.TaskService;
import com.example.crm_backend.utils.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskScheduler {
    private final TaskRepository task_repository;

    private final TaskService task_service;

    private final NotificationService notification_service;

    @Autowired
    public TaskScheduler(TaskRepository task_repository, TaskService task_service, NotificationService notification_service) {
        this.task_repository = task_repository;
        this.task_service = task_service;
        this.notification_service = notification_service;
    }

    @Scheduled(cron = "0 * * * * *") // Chạy mỗi phút
    public synchronized void checkExpiredTasks() {
        Long now = Timer.now();

        List<Task> tasks = task_repository.findExpiredTasks(now);
        if (tasks.isEmpty()) return; // Không cần chạy nếu không có task

        tasks.forEach(task -> {
            task_service.calculateProcess(task);
            task_service.checkExpiredTask(task);
        });

        task_repository.saveAll(tasks);
    }

}
