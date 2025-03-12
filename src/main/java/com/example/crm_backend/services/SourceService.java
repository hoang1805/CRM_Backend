package com.example.crm_backend.services;

import com.example.crm_backend.dtos.SourceDTO;
import com.example.crm_backend.entities.source.Source;
import com.example.crm_backend.entities.source.SourceValidator;
import com.example.crm_backend.entities.user.User;
import com.example.crm_backend.enums.Role;
import com.example.crm_backend.events.SourceEvent;
import com.example.crm_backend.repositories.SourceRepository;
import com.example.crm_backend.utils.ObjectMapper;
import com.example.crm_backend.utils.Timer;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
public class SourceService {
    private final SourceRepository source_repository;

    private final SystemService system_service;

    private final SearchEngine search_engine;

    private final ApplicationEventPublisher event_publisher;

    public SourceService(SourceRepository source_repository, SystemService systemService, SearchEngine searchEngine, ApplicationEventPublisher eventPublisher) {
        this.source_repository = source_repository;
        system_service = systemService;
        this.search_engine = searchEngine;
        event_publisher = eventPublisher;
    }

    public List<Source> getAll() {
        return source_repository.findAll(Sort.by(DESC, "id"));
    }

    public List<Source> getAllBySystemId(Long system_id) {
        return source_repository.findBySystemId(system_id, Sort.by(DESC, "id"));
    }

    public boolean isExist(Source source) {
        String code = source.getCode();
        return !code.isEmpty() && source_repository.existsByCodeAndSystemId(code, source.getSystemId());
    }

    public Source getByCode(String code, Long system_id) {
        if (code.isEmpty() || system_id == null) {
            return null;
        }

        return source_repository.findByCodeAndSystemId(code, system_id);
    }

    public Source getSource(Long id) {
        return source_repository.getReferenceById(id);
    }

    public boolean isExistById(Long id) {
        return id != null && source_repository.existsById(id);
    }

    @Transactional
    public Source create(SourceDTO source_DTO, User creator) {
        Source source = new Source();
        ObjectMapper.mapAll(source_DTO, source);

        if (!system_service.existsById(creator.getSystemId())) {
            throw new IllegalStateException("Invalid system id: " + creator.getSystemId());
        }

        try {
            SourceValidator validator = new SourceValidator(source, this);
            source.setSystemId(creator.getSystemId());
            validator.validate();
            source.setCreatorId(creator.getId());
            source.setCreatedAt(Timer.now());
            source.setLastUpdate(Timer.now());

            source = source_repository.save(source);
            event_publisher.publishEvent(SourceEvent.created(source, this));

            return source;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    @Transactional
    public Source edit(Long source_id, SourceDTO source_DTO) {
        Source source = getSource(source_id);
        if (source == null) {
            throw new IllegalStateException("Invalid source. Please try again");
        }

        if (!system_service.existsById(source.getSystemId())) {
            throw new IllegalStateException("Invalid system id: " + source.getSystemId());
        }

        Source new_source = new Source();
        ObjectMapper.mapAll(source_DTO, new_source);
        new_source.setSystemId(source.getSystemId());

        source.setName(new_source.getName());
        source.setParentId(new_source.getParentId());

        try {
            SourceValidator validator = new SourceValidator(source, this);
            validator.validate();
            if (!Objects.equals(source.getCode(), new_source.getCode())) {
                if (isExist(new_source)) {
                    throw new IllegalArgumentException("Code has already exist. Please try again");
                }
            }
            source.setCode(new_source.getCode());
            source.setLastUpdate(Timer.now());

            source = source_repository.save(source);
            event_publisher.publishEvent(SourceEvent.edited(source, this));

            return source;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    @Transactional
    public void delete(Long id) {
        if (id == null || !isExistById(id)) {
            throw new IllegalStateException("Invalid source. Please try again");
        }

        Source source = getSource(id);
        source_repository.deleteById(id);

        event_publisher.publishEvent(SourceEvent.deleted(source, this));
    }

    public List<Source> search(String query, User user) {
        if (user.getRole() == Role.SUPER_ADMIN) {
            return source_repository.searchSources(query);
        }

        return source_repository.searchSources(query, user.getSystemId());
    }

    public SearchEngine getSearchEngine() {
        return search_engine;
    }
}
