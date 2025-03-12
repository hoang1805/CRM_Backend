package com.example.crm_backend.events;

import com.example.crm_backend.enums.Event;
import com.example.crm_backend.services.RelationshipService;

public class RelationshipEvent extends AppEvent{
    private final RelationshipService relationship_service;

    private RelationshipEvent(Object source, Event event, RelationshipService relationship_service) {
        super(source, event);
        this.relationship_service = relationship_service;
    }

    public static RelationshipEvent created(Object source, RelationshipService relationship_service) {
        return new RelationshipEvent(source, Event.CREATED, relationship_service);
    }

    public static RelationshipEvent deleted(Object source, RelationshipService relationship_service) {
        return new RelationshipEvent(source, Event.DELETED, relationship_service);
    }

    public static RelationshipEvent edited(Object source, RelationshipService relationship_service) {
        return new RelationshipEvent(source, Event.EDITED, relationship_service);
    }

    @Override
    public RelationshipService getService() {
        return relationship_service;
    }
}
