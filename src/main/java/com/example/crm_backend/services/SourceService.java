package com.example.crm_backend.services;

import com.example.crm_backend.dtos.SourceDTO;
import com.example.crm_backend.entities.source.Source;
import com.example.crm_backend.entities.source.SourceValidator;
import com.example.crm_backend.entities.user.User;
import com.example.crm_backend.repositories.SourceRepository;
import com.example.crm_backend.utils.ObjectMapper;
import com.example.crm_backend.utils.Timer;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
public class SourceService {
    private final SourceRepository source_repository;

    public SourceService(SourceRepository source_repository) {
        this.source_repository = source_repository;
    }

    public List<Source> getAll() {
        return source_repository.findAll(Sort.by(DESC, "id"));
    }

    public boolean isExist(Source source) {
        String code = source.getCode();
        return !code.isEmpty() && source_repository.existsByCode(code);
    }

    public Source getSource(Long id) {
        return source_repository.getReferenceById(id);
    }

    public boolean isExistById(Long id) {
        return id != null && source_repository.existsById(id);
    }

    public Source create(SourceDTO source_DTO, User creator) {
        Source source = new Source();
        ObjectMapper.mapAll(source_DTO, source);

        try {
            SourceValidator validator = new SourceValidator(source, this);
            validator.validate();
            source.setCreatorId(creator.getId());
            source.setCreatedAt(Timer.now());
            source.setLastUpdate(Timer.now());
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }

        return source_repository.save(source);
    }

    public Source edit(Long source_id, SourceDTO source_DTO) {
        Source source = getSource(source_id);
        if (source == null) {
            throw new IllegalStateException("Invalid source. Please try again");
        }

        Source new_source = new Source();
        ObjectMapper.mapAll(source_DTO, new_source);
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
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }

        return source_repository.save(source);
    }

    public void delete(Long id) {
        if (id == null || !isExistById(id)) {
            throw new IllegalStateException("Invalid source. Please try again");
        }

        source_repository.deleteById(id);
    }

    public List<Source> search(String query) {
        return source_repository.searchSources(query);
    }
}
