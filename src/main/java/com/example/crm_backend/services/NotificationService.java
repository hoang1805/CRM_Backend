package com.example.crm_backend.services;

import aj.org.objectweb.asm.commons.Remapper;
import com.example.crm_backend.dtos.NotificationDTO;
import com.example.crm_backend.entities.notification.Notification;
import com.example.crm_backend.entities.user.User;
import com.example.crm_backend.repositories.NotificationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notification_repository;

    private final UserService user_service;

    private final SimpMessagingTemplate messaging_template;

    @Autowired
    public NotificationService(NotificationRepository notification_repository, UserService userService, SimpMessagingTemplate messagingTemplate) {
        this.notification_repository = notification_repository;
        user_service = userService;
        messaging_template = messagingTemplate;
    }

    public List<Notification> getByUser(User user) {
        return notification_repository.findByTargetIdAndSystemId(user.getId(), user.getSystemId());
    }

    public long countByUser(User user) {
//        System.out.println("User ID: " + user.getId());
//        System.out.println("System ID: " + user.getSystemId());
//        System.out.println("Query Result: " +
//                notification_repository.countByTargetIdAndSystemIdAndIsRead(user.getId(), user.getSystemId(), false));

        return notification_repository.countByTargetIdAndSystemIdAndIsRead(user.getId(), user.getSystemId(), false);
    }

    public Notification markAsRead(User user, Long notification_id) {
        Notification notification = notification_repository.findById(notification_id).orElse(null);
        if (notification == null) {
            throw new IllegalStateException("Notification not found");
        }

        if (!Objects.equals(notification.getSystemId(), user.getSystemId())) {
            throw new IllegalStateException("Notification not found");
        }

        if (!Objects.equals(notification.getTargetId(), user.getId())) {
            throw new IllegalStateException("Notification not found");
        }

        notification.setRead(true);
        return notification_repository.save(notification);
    }

    @Transactional
    public void markAllAsRead(User user) {
        notification_repository.markAllAsReadByUser(user.getId(), user.getSystemId());
    }

    public void sendToUser(Notification notification) {
        if (notification == null) {
            return ;
        }

        if (notification.getId() == null) {
            return ;
        }

        if (notification.getSystemId() == null) {
            return ;
        }

        messaging_template.convertAndSend("/topic/notification/" + notification.getTargetId(), notification.release());
    }

    private List<Long> unique(List<Long> ids) {
        return ids.stream().distinct().collect(Collectors.toList());
    }

    public void notify(User user, String title, List<Long> target_ids, String message, String object_name) {
        List<Notification> notifications = new ArrayList<>();
        for (Long target_id : unique(target_ids)) {
            User target_user = user_service.getUser(target_id);
            if (target_user == null) {
                continue;
            }

            if (!Objects.equals(target_user.getSystemId(), user.getSystemId())) {
                continue;
            }

            if (Objects.equals(target_user.getId(), user.getId())) {
                continue;
            }

            Notification notification = new Notification(title, target_id, user.getId(), message, Map.of("object_name", object_name), user.getSystemId());
            notifications.add(notification);
        }

        if (!notifications.isEmpty()) {
            notification_repository.saveAll(notifications);
        }

        for (Notification notification : notifications) {
            sendToUser(notification);
        }
    }

    public void notify(User user, String title, List<Long> target_ids, String message, String object_name, Long system_id) {
        if (system_id == null) {
            return;
        }

        List<Notification> notifications = new ArrayList<>();
        for (Long target_id : unique(target_ids)) {
            User target_user = user_service.getUser(target_id);
            if (target_user == null) {
                continue;
            }

            if (!Objects.equals(target_user.getSystemId(), system_id)) {
                continue;
            }

            if (Objects.equals(target_user.getId(), user.getId())) {
                continue;
            }

            Notification notification = new Notification(title, target_id, user.getId(), message, Map.of("object_name", object_name), system_id);
            notifications.add(notification);
        }

        if (!notifications.isEmpty()) {
            notification_repository.saveAll(notifications);
        }

        for (Notification notification : notifications) {
            sendToUser(notification);
        }
    }

    public void notify(User user, String title, List<Long> target_ids, String message, String object_name, String url) {
        List<Notification> notifications = new ArrayList<>();
        for (Long target_id : unique(target_ids)) {
            User target_user = user_service.getUser(target_id);
            if (target_user == null) {
                continue;
            }

            if (!Objects.equals(target_user.getSystemId(), user.getSystemId())) {
                continue;
            }

            if (Objects.equals(target_user.getId(), user.getId())) {
                continue;
            }

            Notification notification = new Notification(title, target_id, user.getId(), message, Map.of("object_name", object_name), url, user.getSystemId());
            notifications.add(notification);
        }

        if (!notifications.isEmpty()) {
            notification_repository.saveAll(notifications);
        }

        for (Notification notification : notifications) {
            sendToUser(notification);
        }
    }

    public void notify(User user, String title, List<Long> target_ids, String message, String object_name, String url, Long system_id) {
        if (system_id == null) {
            return;
        }

        List<Notification> notifications = new ArrayList<>();
        for (Long target_id : unique(target_ids)) {
            User target_user = user_service.getUser(target_id);
            if (target_user == null) {
                continue;
            }

            if (!Objects.equals(target_user.getSystemId(), system_id)) {
                continue;
            }

            if (Objects.equals(target_user.getId(), user.getId())) {
                continue;
            }

            Notification notification = new Notification(title, target_id, user.getId(), message, Map.of("object_name", object_name), url, system_id);
            notifications.add(notification);
        }

        if (!notifications.isEmpty()) {
            notification_repository.saveAll(notifications);
        }

        for (Notification notification : notifications) {
            sendToUser(notification);
        }
    }

    public void notifyAll(User user, String title, List<Long> except_ids, String message, Map<String, String> additional, String url, Long system_id) {
        if (system_id == null) {
            return;
        }

        List<Long> user_ids = user_service.getUsersBySystem(system_id).stream().map(User::getId).toList();
        except_ids.add(user.getId());

        user_ids = except(user_ids, except_ids);

        List<Notification> notifications = new ArrayList<>();

        for (Long target_id : user_ids) {
            User target_user = user_service.getUser(target_id);
            if (target_user == null) {
                continue;
            }

            if (!Objects.equals(target_user.getSystemId(), system_id)) {
                continue;
            }

            Notification notification = new Notification(title, target_id, user.getId(), message, additional, url, system_id);
            notifications.add(notification);
        }

        if (!notifications.isEmpty()) {
            notification_repository.saveAll(notifications);
        }

        for (Notification notification : notifications) {
            sendToUser(notification);
        }
    }

    public void systemNotify(String title, List<Long> target_ids, String message, String object_name, String url, Long system_id) {
        if (system_id == null) {
            return;
        }

        List<Notification> notifications = new ArrayList<>();
        for (Long target_id : unique(target_ids)) {
            User target_user = user_service.getUser(target_id);
            if (target_user == null) {
                continue;
            }

            if (!Objects.equals(target_user.getSystemId(), system_id)) {
                continue;
            }

            Notification notification = new Notification(title, target_id, 0L, message, Map.of("object_name", object_name), url, system_id);
            notifications.add(notification);
        }

        if (!notifications.isEmpty()) {
            notification_repository.saveAll(notifications);
        }

        for (Notification notification : notifications) {
            sendToUser(notification);
        }
    }

    public Page<Notification> paginate(int ipp, int page, User user) {
        Pageable request = PageRequest.of(page, ipp, Sort.by(Sort.Direction.DESC, "id"));
        return notification_repository.getNotifications(user.getId(), user.getSystemId(), request);
    }

    private List<Long> except(List<Long> ids, List<Long> except_ids) {
        return ids.stream().filter(id -> !except_ids.contains(id)).collect(Collectors.toList());
    }
}
