package com.example.crm_backend.services;

import com.example.crm_backend.dtos.RelationshipDTO;
import com.example.crm_backend.entities.relationship.Relationship;
import com.example.crm_backend.entities.relationship.RelationshipValidator;
import com.example.crm_backend.entities.user.User;
import com.example.crm_backend.repositories.RelationshipRepository;
import com.example.crm_backend.utils.ObjectMapper;
import com.example.crm_backend.utils.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.List;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
public class RelationshipService {

    private final RelationshipRepository relationship_repository;

    private final SystemService system_service;

    @Autowired
    public RelationshipService(RelationshipRepository relationship_repository, SystemService systemService) {
        this.relationship_repository = relationship_repository;
        system_service = systemService;
    }

    public List<Relationship> getAll() {
        return relationship_repository.findAll(Sort.by(DESC, "id"));
    }

    public List<Relationship> getAllBySystemId(Long system_id) {
        return relationship_repository.findBySystemId(system_id);
    }

    public Relationship getRelationship(Long id) {
        return relationship_repository.getReferenceById(id);
    }

     public boolean isExistById(Long id) {
        return id != null && relationship_repository.existsById(id);
     }

     public Relationship create(RelationshipDTO relationship_DTO, User creator) {
        Relationship relationship = new Relationship();
         ObjectMapper.mapAll(relationship_DTO, relationship);

         try {
             RelationshipValidator validator = new RelationshipValidator(relationship, this);
             validator.validate();
             relationship.setCreatorId(creator.getId());
             relationship.setCreatedAt(Timer.now());
             relationship.setLastUpdate(Timer.now());
         } catch (Exception e) {
             throw new IllegalStateException(e.getMessage());
         }

         return relationship_repository.save(relationship);
     }

     public Relationship edit(Long relationship_id, RelationshipDTO relationship_DTO) {
        Relationship relationship = getRelationship(relationship_id);
        if (relationship == null) {
            throw new IllegalStateException("Invalid relationship. Please try again");
        }

        Relationship new_relationship = new Relationship();
        ObjectMapper.mapAll(relationship_DTO, new_relationship);

        relationship.setName(new_relationship.getName());
        relationship.setColor(new_relationship.getColor());
        relationship.setDescription(new_relationship.getDescription());

        try {
            RelationshipValidator validator = new RelationshipValidator(relationship, this);
            validator.validate();
            relationship.setLastUpdate(Timer.now());
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }

        return relationship_repository.save(relationship);
     }

    public Relationship editColor(Long relationship_id, RelationshipDTO relationship_DTO) {
        Relationship relationship = getRelationship(relationship_id);
        if (relationship == null) {
            throw new IllegalStateException("Invalid relationship. Please try again");
        }

        Relationship new_relationship = new Relationship();
        ObjectMapper.mapAll(relationship_DTO, new_relationship);

        relationship.setColor(new_relationship.getColor());

        try {
            RelationshipValidator validator = new RelationshipValidator(relationship, this);

            validator.validate();
            relationship.setLastUpdate(Timer.now());
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }

        return relationship_repository.save(relationship);
    }

     public void delete(Long id) {
        if (id == null || !isExistById(id)) {
            throw new IllegalStateException("Invalid relationship. Please try again");
        }

         relationship_repository.deleteById(id);
     }
}
