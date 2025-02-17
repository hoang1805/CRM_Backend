package com.example.crm_backend.services;

import com.example.crm_backend.entities.user.User;
import com.example.crm_backend.entities.user.UserValidator;
import com.example.crm_backend.repositories.UserRepository;
import com.example.crm_backend.utils.Encoder;
import com.example.crm_backend.utils.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository user_repository;

    @Autowired
    public UserService(UserRepository user_repository){
        this.user_repository = user_repository;
    }

    public List<User> getUsers(){
        return user_repository.findAll();
    }

    public User getUser(Long id){
        return user_repository.findById(id).orElse(null);
    }

    public boolean isExisted(String username, String email){
        return user_repository.existsByUsernameOrEmail(username, email);
    }

    public User findByEmail(String email){
        Optional<User> user = user_repository.findByEmail(email);
        return user.orElse(null);
    }

    public User findByUsername(String username){
        Optional<User> user = user_repository.findByUsername(username);
        return user.orElse(null);
    }

    public User createUser(User user){
        try {
            UserValidator validator = new UserValidator(user, this);
            validator.validate();
        } catch (Exception e) {
            throw e;
        }

        user.setPassword(Encoder.hashPassword(user.getPassword()));
        user.setLastUpdate(Timer.now());
        user.setCreatedAt(Timer.now());
        return user_repository.save(user);
    }

    @Transactional
    public void deleteUser(Long id){
        boolean exist = user_repository.existsById(id);
        if (!exist) {
            throw new IllegalStateException("User does not exist");
        }

        user_repository.deleteById(id);
    }

    public void updateUser(){

    }

    public Page<User> paginate(int ipp, int page){
        Pageable request = PageRequest.of(page, ipp, Sort.by(Sort.Direction.DESC, "id"));
        return user_repository.findAll(request);
    }

    public List<User> loadUsers(List <Long> user_ids) {
        user_ids = user_ids.stream().distinct().collect(Collectors.toList());
        return user_repository.findByIdIn(user_ids);
    }

    public List<User> searchUsers(String query) {
        return user_repository.searchUsers(query);
    }
}
