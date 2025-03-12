package com.example.crm_backend.events;

import com.example.crm_backend.enums.Event;
import com.example.crm_backend.services.SourceService;

public class SourceEvent extends AppEvent{
    private final SourceService source_service;

    private SourceEvent(Object source, Event event, SourceService source_service) {
        super(source, event);
        this.source_service = source_service;
    }

    public static SourceEvent created(Object source, SourceService source_service) {
        return new SourceEvent(source, Event.CREATED, source_service);
    }

    public static SourceEvent deleted(Object source, SourceService source_service) {
        return new SourceEvent(source, Event.DELETED, source_service);
    }

    public static SourceEvent edited(Object source, SourceService source_service) {
        return new SourceEvent(source, Event.EDITED, source_service);
    }

    @Override
    public SourceService getService() {
        return source_service;
    }
}
