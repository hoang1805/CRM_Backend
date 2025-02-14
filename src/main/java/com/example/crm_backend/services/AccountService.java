package com.example.crm_backend.services;

import com.example.crm_backend.dtos.AccountDTO;
import com.example.crm_backend.entities.account.Account;
import com.example.crm_backend.entities.account.AccountValidator;
import com.example.crm_backend.entities.user.User;
import com.example.crm_backend.entities.user.UserValidator;
import com.example.crm_backend.repositories.AccountRepository;
import com.example.crm_backend.repositories.UserRepository;
import com.example.crm_backend.utils.ObjectMapper;
import com.example.crm_backend.utils.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService {
    private final AccountRepository account_repository;

    private final UserRepository user_repository;

    @Autowired
    public AccountService(AccountRepository account_repository, UserRepository user_repository) {
        this.account_repository = account_repository;
        this.user_repository = user_repository;
    }

    public List<Account> getAll(){
        return account_repository.findAll();
    }

    public Page<Account> paginate(int ipp, int page){
        Pageable request = PageRequest.of(page, ipp, Sort.by(Sort.Direction.DESC, "id"));
        return account_repository.findAll(request);
    }

    public Page<Account> paginate(int ipp, int page, User user){
        Pageable request = PageRequest.of(page, ipp, Sort.by(Sort.Direction.DESC, "id"));
        return account_repository.findAllByUser(user.getId(), request);
    }

    public Account createAccount(AccountDTO data, User creator) {
        Account account = new Account();
        ObjectMapper.mapAll(data, account);
        try {
            AccountValidator validator = new AccountValidator(account, this);
            validator.validate();
        } catch (Exception e) {
            throw e;
        }

        account.setCreatorId(creator.getId());
        account.setLastUpdate(Timer.now());
        account.setCreatedAt(Timer.now());

//        return account_repository.save(account);
        return account;
    }

    public boolean isExist(Account account) {
        String code = account.getCode();
        return !code.isEmpty() && account_repository.existsByCode(code);
    }

    public Long count(User user){
        return account_repository.countByUser(user.getId());
    }

    public Long count(){
        return account_repository.count();
    }

    public boolean isValidUser(Long user_id) {
        if (user_id == null) {
            return false;
        }
        return user_repository.existsById(user_id);
    }
}
