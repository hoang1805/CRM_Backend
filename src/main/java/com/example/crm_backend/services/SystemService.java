package com.example.crm_backend.services;

import com.example.crm_backend.dtos.SystemDTO;
import com.example.crm_backend.entities.system.System;
import com.example.crm_backend.entities.system.SystemValidator;
import com.example.crm_backend.repositories.*;
import com.example.crm_backend.utils.ObjectMapper;
import com.example.crm_backend.utils.Timer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SystemService {
    private final SystemRepository system_repository;

    private final UserRepository user_repository;

    private final TaskRepository task_repository;

    private final SourceRepository source_repository;

    private final RelationshipRepository relationship_repository;

    private final FeedbackRepository feedback_repository;

    private final AccountRepository account_repository;

    private final AccountProductRepository account_product_repository;

    public SystemService(SystemRepository systemRepository, UserRepository userRepository, TaskRepository taskRepository, SourceRepository sourceRepository, RelationshipRepository relationshipRepository, FeedbackRepository feedbackRepository, AccountRepository accountRepository, AccountProductRepository accountProductRepository) {
        system_repository = systemRepository;
        user_repository = userRepository;
        task_repository = taskRepository;
        source_repository = sourceRepository;
        relationship_repository = relationshipRepository;
        feedback_repository = feedbackRepository;
        account_repository = accountRepository;
        account_product_repository = accountProductRepository;
    }

    public List<System> getAll() {
        return system_repository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    public Page<System> paginate(int ipp, int page) {
        Pageable request = PageRequest.of(page, ipp, Sort.by(Sort.Direction.DESC, "id"));
        return system_repository.findAll(request);
    }

    public System getById(Long id) {
        return system_repository.getReferenceById(id);
    }

    public boolean existsById(Long id) {
        return system_repository.existsById(id);
    }

    public System create(SystemDTO dto) {
        System system = new System();
        ObjectMapper.mapAll(dto, system);

        SystemValidator validator = new SystemValidator(system, this);
        validator.validate();

        system.setCreatedAt(Timer.now());
        system.setLastUpdate(Timer.now());

        return system_repository.save(system);
    }

    public System edit(Long id, SystemDTO dto) {
        System system = this.getById(id);
        if (system == null) {
            throw new IllegalStateException("System not found");
        }

        system.setName(dto.getName());
        system.setMaxUser(dto.getMaxUser());

        SystemValidator validator = new SystemValidator(system, this);
        validator.validate();

        system.setLastUpdate(Timer.now());

        return system_repository.save(system);
    }

    @Transactional
    public void delete(Long id) {
        system_repository.deleteById(id);
        onDelete(id);
    }

    @Transactional
    public void onDelete(Long id) {
        account_repository.deleteBySystemId(id);
        account_product_repository.deleteBySystemId(id);
        feedback_repository.deleteBySystemId(id);
        task_repository.deleteBySystemId(id);
        source_repository.deleteBySystemId(id);
        relationship_repository.deleteBySystemId(id);
        user_repository.deleteBySystemId(id);
    }
}
