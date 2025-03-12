package com.example.crm_backend.entities.relationship;

import com.example.crm_backend.entities.HasListener;
import com.example.crm_backend.events.RelationshipEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class RelationshipListener implements HasListener<RelationshipEvent> {
    @EventListener
    public void handleEvent(RelationshipEvent event) {
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

    private void onCreate(RelationshipEvent event) {
        event.getService().getSearchEngine().remove("relationships");
    }

    private void onEdit(RelationshipEvent event) {
        event.getService().getSearchEngine().remove("relationships");
    }

    private void onDelete(RelationshipEvent event) {
        event.getService().getSearchEngine().remove("relationships");
    }
}
