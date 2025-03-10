package com.example.crm_backend.entities.source;

import com.example.crm_backend.services.SourceService;
import com.example.crm_backend.utils.Validator;

import java.util.Objects;

public class SourceValidator extends Validator {
    private final Source source;

    private final SourceService source_service;

    public SourceValidator(Source source, SourceService source_service) {
        this.source = source;
        this.source_service = source_service;
    }

    public SourceValidator validParent() {
//        Long parent_id = source.getParentId();
//
//        if (parent_id != null && source.getId() != null && Objects.equals(parent_id, source.getId())) {
//            throw new IllegalStateException("Parent must be self object");
//        }
//
//        if (parent_id != null && !source_service.isExistById(parent_id)) {
//            throw new IllegalStateException("Invalid parent. Please try again");
//        }

        return this;
    }

    public SourceValidator validCode(){
        String code = source.getCode();

        if (code == null || code.isEmpty()) {
            throw new IllegalStateException("Code is empty. Please try again");
        }

        if (source.getId() == null && source_service.isExist(source)) {
            throw new IllegalStateException("Code has already exist. Please try again");
        }

//        if (source.getId() != null) {
//            Source n_source = source_service.getByCode(code, source.getSystemId());
//            if (n_source != null) {
//                throw new IllegalStateException("Code has already exist. Please try again");
//            }
//        }

        return this;
    }

    public SourceValidator validName(){
        String name = source.getName();

        if (name == null || name.isEmpty()) {
            throw new IllegalStateException("Name is empty. Please try again");
        }

        return this;
    }

    public void validate(){
        validName().validCode().validParent();
    }
}
