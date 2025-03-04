package com.example.crm_backend.services.cronjob;

import com.example.crm_backend.entities.remind.Remind;
import com.example.crm_backend.entities.task.Task;
import com.example.crm_backend.repositories.RemindRepository;
import com.example.crm_backend.services.NotificationService;
import com.example.crm_backend.services.RemindService;
import com.example.crm_backend.utils.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Reminder {
    private final RemindService remind_service;

    private final NotificationService notification_service;

    private final RemindRepository remind_repository;

    @Autowired
    public Reminder(RemindService remind_service, NotificationService notification_service, RemindRepository remind_repository) {
        this.remind_service = remind_service;
        this.notification_service = notification_service;
        this.remind_repository = remind_repository;
    }

    @Scheduled(cron = "0 * * * * *") // Chạy mỗi phút
    public synchronized void checkReminders() {
        Long now = Timer.now();

        List<Remind> reminds = remind_repository.findReminder(now);
        if (reminds.isEmpty()) {
            return;
        }

        reminds.forEach(remind -> {
            notification_service.notify(remind);
            remind.setReminded(true);
        });

        remind_repository.saveAll(reminds);
    }
}
