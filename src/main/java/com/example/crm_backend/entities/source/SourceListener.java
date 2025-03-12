package com.example.crm_backend.entities.source;

import com.example.crm_backend.entities.HasListener;
import com.example.crm_backend.events.SourceEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class SourceListener implements HasListener<SourceEvent> {
    @EventListener
    public void handleEvent(SourceEvent event) {
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

    private void onCreate(SourceEvent event) {
        event.getService().getSearchEngine().remove("sources");
    }

    private void onEdit(SourceEvent event) {
        event.getService().getSearchEngine().remove("sources");
    }

    private void onDelete(SourceEvent event) {
        event.getService().getSearchEngine().remove("sources");
    }
}
