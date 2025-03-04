package com.example.crm_backend.repositories;

import com.example.crm_backend.entities.remind.Remind;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RemindRepository extends JpaRepository<Remind, Long> {

    @Query("SELECT r FROM Remind r WHERE r.remindTime < now AND r.enabled = true AND r.reminded = false")
    List<Remind> findReminder(@Param("now") Long now);
}
