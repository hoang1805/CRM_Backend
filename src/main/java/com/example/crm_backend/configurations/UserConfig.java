package com.example.crm_backend.configurations;

import com.example.crm_backend.entities.user.User;
import com.example.crm_backend.enums.Gender;
import com.example.crm_backend.enums.Role;
import com.example.crm_backend.repositories.UserRepository;
import com.example.crm_backend.utils.Encoder;
import com.example.crm_backend.utils.Timer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Time;

@Configuration
public class UserConfig {

    @Bean
    CommandLineRunner commandLineRunner(UserRepository user_repository){
        return args -> {
            User admin = new User("admin", "Super Admin", "", "", Gender.OTHER, 0L,
                    "Super Admin", Role.SUPER_ADMIN, "", Encoder.hashPassword("123456"), 0L, 0L, 0L, 0L);
            admin.setCreatedAt(Timer.now());
            admin.setLastUpdate(Timer.now());
            if (user_repository.findByUsername(admin.getUsername()).isEmpty()) {
                user_repository.save(admin);
            }
        };
    }
}
