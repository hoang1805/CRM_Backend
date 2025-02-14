package com.example.crm_backend.entities.relationship;

import com.example.crm_backend.services.RelationshipService;
import com.example.crm_backend.utils.Validator;

public class RelationshipValidator extends Validator {
    private Relationship relationship;

    private RelationshipService relationship_service;

    public RelationshipValidator(Relationship relationship, RelationshipService relationship_service) {
        this.relationship = relationship;
        this.relationship_service = relationship_service;
    }

    public RelationshipValidator validName() {
        String name = relationship.getName();

        if (name == null || name.isEmpty()) {
            throw new IllegalStateException("Name is empty. Please try again");
        }

        return this;
    }

    public RelationshipValidator validColor() {
        if (!isValidHexColor(relationship.getColor())) {
            throw new IllegalStateException("Invalid color. Please try again");
        }

        return this;
    }

    public void validate() {
        validName().validColor();
    }
}
