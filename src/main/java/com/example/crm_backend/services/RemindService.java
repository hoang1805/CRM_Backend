package com.example.crm_backend.services;

import com.example.crm_backend.dtos.RemindDTO;
import com.example.crm_backend.entities.remind.Remind;
import com.example.crm_backend.entities.remind.RemindValidator;
import com.example.crm_backend.repositories.RemindRepository;
import com.example.crm_backend.utils.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RemindService {
    private final RemindRepository remind_repository;

    @Autowired
    public RemindService(RemindRepository remind_repository) {
        this.remind_repository = remind_repository;
    }

    public Remind getRemind(Long id) {
        return remind_repository.findById(id).orElse(null);
    }

    public Remind create(String message, Long remindTime, Map<String, String> additional, List<Long> userIds, String url, Long systemId) {
        try {
            Remind remind = new Remind(message, remindTime, additional, userIds, url, systemId);

            RemindValidator validator = new RemindValidator(remind, this);
            validator.validate();

            return remind_repository.save(remind);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    public Remind edit(Long id, RemindDTO dto) {
        Remind remind = this.getRemind(id);
        if (remind == null) {
            throw new IllegalStateException("Remind not found");
        }

        try {
            remind.setReminded(false);
            if (dto.getEnabled() != null) {
                remind.setEnabled(dto.getEnabled());
            }

            if (dto.getRemindTime() != null) {
                remind.setRemindTime(dto.getRemindTime());
            }

            if (dto.getAdditional() != null) {
                remind.setAdditional(dto.getAdditional());
            }

            if (dto.getUrl() != null) {
                remind.setUrl(dto.getUrl());
            }

            if (dto.getMessage() != null) {
                remind.setMessage(dto.getMessage());
            }

            if (dto.getUserIds() != null) {
                remind.setUserIds(dto.getUserIds());
            }

            RemindValidator validator = new RemindValidator(remind, this);
            validator.validate();

            return remind_repository.save(remind);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    public Remind edit(Long id, Long remindTime) {
        Remind remind = this.getRemind(id);
        if (remind == null) {
            throw new IllegalStateException("Remind not found");
        }

        try {
            remind.setReminded(false);
            if (remindTime < Timer.now()) {
                remind.setReminded(true);
            }
            remind.setRemindTime(remindTime);

            RemindValidator validator = new RemindValidator(remind, this);
            validator.validate();

            return remind_repository.save(remind);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    public void delete(Long id) {
//        Remind remind = this.getRemind(id);
//        if (remind == null) {
//            throw new IllegalStateException("Remind not found");
//        }

        if (id == null) {
            return ;
        }

        remind_repository.deleteById(id);
    }

}
