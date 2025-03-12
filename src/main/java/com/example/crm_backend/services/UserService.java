package com.example.crm_backend.services;

import aj.org.objectweb.asm.commons.Remapper;
import com.example.crm_backend.dtos.UserDTO;
import com.example.crm_backend.dtos.UserPasswordDTO;
import com.example.crm_backend.entities.system.System;
import com.example.crm_backend.entities.user.User;
import com.example.crm_backend.entities.user.UserValidator;
import com.example.crm_backend.enums.Role;
import com.example.crm_backend.events.UserEvent;
import com.example.crm_backend.repositories.UserRepository;
import com.example.crm_backend.utils.Encoder;
import com.example.crm_backend.utils.ObjectMapper;
import com.example.crm_backend.utils.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository user_repository;

    private final SystemService system_service;

    private final SearchEngine search_engine;

    private final ApplicationEventPublisher event_publisher;

    @Autowired
    public UserService(UserRepository user_repository, SystemService systemService, SearchEngine searchEngine, ApplicationEventPublisher eventPublisher) {
        this.user_repository = user_repository;
        system_service = systemService;
        search_engine = searchEngine;
        event_publisher = eventPublisher;
    }

    public SearchEngine getSearchEngine() {
        return search_engine;
    }

    public List<User> getUsers() {
        return user_repository.findAll();
    }

    public List<User> getUsersBySystem(Long system_id) {
        return user_repository.findBySystemId(system_id);
    }

    public User getUserBySystem(Long id, Long system_id) {
        return user_repository.findByIdAndSystemId(id, system_id).orElse(null);
    }

    public User getUser(Long id) {
        return user_repository.findById(id).orElse(null);
    }

    public boolean isExisted(String username, String email) {
        return user_repository.existsByUsernameOrEmail(username, email);
    }

    public User findByEmail(String email) {
        Optional<User> user = user_repository.findByEmail(email);
        return user.orElse(null);
    }

    public User findByUsername(String username) {
        Optional<User> user = user_repository.findByUsername(username);
        return user.orElse(null);
    }

    public long countBySystemId(Long system_id) {
        return user_repository.countBySystemId(system_id);
    }

    @Transactional
    public User createUser(UserDTO dto, User creator) {
        Long system_id = creator.getSystemId();
        if (creator.getRole() == Role.SUPER_ADMIN) {
            system_id = dto.getSystemId();
        }

        System system = system_service.getById(system_id);
        if (system == null) {
            throw new RuntimeException("System not found");
        }

        if (this.countBySystemId(system_id) >= system.getMaxUser()) {
            throw new RuntimeException("Can't create user after system max user");
        }

        User user = new User();
        ObjectMapper.mapAll(dto, user);

        user.setPassword("123456");
        user.setRole(Role.STAFF);
        user.setSystemId(system_id);

        UserValidator validator = new UserValidator(user, this);
        validator.validate();

        user.setPassword(Encoder.hashPassword(user.getPassword()));
        user.setLastUpdate(Timer.now());
        user.setCreatedAt(Timer.now());
        user.setCreatorId(creator.getId());

        user = user_repository.save(user);
        event_publisher.publishEvent(UserEvent.created(user, this));

        return user;
    }

    @Transactional
    public void deleteUser(Long id) {
        boolean exist = user_repository.existsById(id);
        if (!exist) {
            throw new IllegalStateException("User does not exist");
        }

        User user = getUser(id);
        user_repository.deleteById(id);

        event_publisher.publishEvent(UserEvent.deleted(user, this));
    }

    @Transactional
    public User updateUser(Long user_id, UserDTO userDTO) {
        User user = getUser(user_id);

        if (user == null) {
            throw new IllegalStateException("User does not exist");
        }

        if (user.getRole() != Role.SUPER_ADMIN && !system_service.existsById(user.getSystemId())) {
            throw new IllegalStateException("User does not exist");
        }

        User newUser = new User();
        ObjectMapper.mapAll(userDTO, newUser);

        user.setName(newUser.getName());
        user.setPhone(newUser.getPhone());
        user.setEmail(newUser.getEmail());
        user.setGender(newUser.getGender());
        user.setBirthday(newUser.getBirthday());
        user.setTitle(newUser.getTitle());
        user.setSign(newUser.getSign());

        try {
            UserValidator validator = new UserValidator(user, this);
            validator.validate();
            user.setLastUpdate(Timer.now());

            user = user_repository.save(user);
            event_publisher.publishEvent(UserEvent.edited(user, this));

            return user;
        } catch (Exception ex) {
            throw new IllegalStateException(ex.getMessage());
        }
    }

    @Transactional
    public User updateUserPassword(Long user_id, UserPasswordDTO userDTO) {
        if (userDTO.getOldPassword() == null || userDTO.getNewPassword() == null || userDTO.getConfirmPassword() == null) {
            throw new IllegalStateException("Some password fields are empty");
        }

        User user = getUser(user_id);
        if (user == null) {
            throw new IllegalStateException("User does not exist");
        }

        if (user.getRole() != Role.SUPER_ADMIN && !system_service.existsById(user.getSystemId())) {
            throw new IllegalStateException("User does not exist");
        }

        if (!Encoder.verifyPassword(userDTO.getOldPassword(), user.getPassword())) {
            throw new IllegalStateException("Password does not match");
        }

        try {
            user.setPassword(userDTO.getNewPassword());
            UserValidator validator = new UserValidator(user, this);
            validator.validPassword();

            if (!Objects.equals(userDTO.getNewPassword(), userDTO.getConfirmPassword())) {
                throw new IllegalStateException("Password confirm does not match");
            }

            user.setPassword(Encoder.hashPassword(user.getPassword()));
            user.setLastUpdate(Timer.now());

            user = user_repository.save(user);
            event_publisher.publishEvent(UserEvent.edited(user, this));

            return user;
        } catch (Exception ex) {
            throw new IllegalStateException(ex.getMessage());
        }
    }

    public Page<User> paginate(int ipp, int page) {
        Pageable request = PageRequest.of(page, ipp, Sort.by(Sort.Direction.DESC, "id"));
        return user_repository.findAll(request);
    }

    public Page<User> paginateBySystem(int ipp, int page, Long systemId) {
        Pageable request = PageRequest.of(page, ipp, Sort.by(Sort.Direction.DESC, "id"));
        return user_repository.findBySystemId(systemId, request);
    }

    public List<User> loadUsers(List<Long> user_ids, User current_user) {
        user_ids = user_ids.stream().distinct().collect(Collectors.toList());
        if (current_user.getRole().equals(Role.SUPER_ADMIN)) {
            return user_repository.findByIdIn(user_ids);
        }

        return user_repository.findByIdInAndSystemId(user_ids, current_user.getSystemId());
    }

    public List<User> searchUsers(String query, User current_user) {
        if (current_user.getRole().equals(Role.SUPER_ADMIN)) {
            return user_repository.searchUsers(query);
        }

        return user_repository.searchUsers(query, current_user.getSystemId());
    }

    @Transactional
    public User grantManager(Long id) {
        User user = getUser(id);
        if (user == null) {
            throw new IllegalStateException("User does not exist");
        }

        if (user.getRole() != Role.SUPER_ADMIN && !system_service.existsById(user.getSystemId())) {
            throw new IllegalStateException("User does not exist");
        }

        user.setRole(Role.MANAGER);
        user.setLastUpdate(Timer.now());

        user = user_repository.save(user);
        event_publisher.publishEvent(UserEvent.edited(user, this));

        return user;
    }

    @Transactional
    public User grantStaff(Long id) {
        User user = getUser(id);
        if (user == null) {
            throw new IllegalStateException("User does not exist");
        }

        if (user.getRole() != Role.SUPER_ADMIN && !system_service.existsById(user.getSystemId())) {
            throw new IllegalStateException("User does not exist");
        }

        user.setRole(Role.STAFF);
        user.setLastUpdate(Timer.now());

        user = user_repository.save(user);
        event_publisher.publishEvent(UserEvent.edited(user, this));

        return user;
    }

    @Transactional
    public User grantAdmin(Long id) {
        User user = getUser(id);
        if (user == null) {
            throw new IllegalStateException("User does not exist");
        }

        if (user.getRole() != Role.SUPER_ADMIN && !system_service.existsById(user.getSystemId())) {
            throw new IllegalStateException("User does not exist");
        }

        user.setRole(Role.ADMIN);
        user.setLastUpdate(Timer.now());

        user = user_repository.save(user);
        event_publisher.publishEvent(UserEvent.edited(user, this));

        return user;
    }

    @Transactional
    public User resetPassword(Long id) {
        User user = getUser(id);
        if (user == null) {
            throw new IllegalStateException("User does not exist");
        }

        if (user.getRole() != Role.SUPER_ADMIN && !system_service.existsById(user.getSystemId())) {
            throw new IllegalStateException("User does not exist");
        }

        user.setPassword(Encoder.hashPassword("123456"));
        user.setLastUpdate(Timer.now());

        user = user_repository.save(user);
        event_publisher.publishEvent(UserEvent.edited(user, this));

        return user;
    }
}
