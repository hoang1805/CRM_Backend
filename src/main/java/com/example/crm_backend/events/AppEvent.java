package com.example.crm_backend.events;

import com.example.crm_backend.enums.Event;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

@Getter
public abstract class AppEvent extends ApplicationEvent {
    private final Event event;

    public AppEvent(Object source, Event event) {
        super(source);
        this.event = event;
    }

    public AppEvent(Object source, Clock clock, Event event) {
        super(source, clock);
        this.event = event;
    }

    public abstract Object getService();
}
