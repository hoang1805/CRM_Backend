package com.example.crm_backend.entities;

import com.example.crm_backend.events.AppEvent;
import org.springframework.context.event.EventListener;

public interface HasListener<T extends AppEvent> {
    @EventListener
    void handleEvent(T event);
}
