package com.example.crm_backend.repositories;

import com.example.crm_backend.entities.system.System;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemRepository extends JpaRepository<System, Long> {

}
