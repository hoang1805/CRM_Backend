package com.example.crm_backend.entities.user;

import com.example.crm_backend.entities.HasListener;
import com.example.crm_backend.events.UserEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class UserListener implements HasListener<UserEvent> {
    @EventListener
    public void handleEvent(UserEvent event) {
        switch (event.getEvent()) {
            case CREATED:
                onCreate(event);
                break;
            case EDITED:
                onEdit(event);
                break;
            case DELETED:
                onDelete(event);
                break;
            default:
                throw new IllegalStateException("Unknown event type: " + event.getEvent());
        }
    }

    private void onCreate(UserEvent event) {
        event.getService().getSearchEngine().remove("users");
    }

    private void onEdit(UserEvent event) {
        event.getService().getSearchEngine().remove("users");
    }

    private void onDelete(UserEvent event) {
        event.getService().getSearchEngine().remove("users");
    }
}
