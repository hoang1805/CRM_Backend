package com.example.crm_backend.repositories;

import com.example.crm_backend.entities.relationship.Relationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RelationshipRepository extends JpaRepository<Relationship, Long> {
    void deleteBySystemId(Long system_id);

    List<Relationship> findBySystemId(Long system_id);
}
