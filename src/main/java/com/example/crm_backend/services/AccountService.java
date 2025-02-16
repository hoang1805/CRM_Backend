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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

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

        return account_repository.save(account);
//        return account;
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

    @Transactional
    public void deleteAccounts(List<Long> ids) {
//        List<Account> accounts = account_repository.findAllById(ids);
//        for (Account account : accounts) {
//            account_repository.delete(account);
//        }
        account_repository.deleteAllByIdInBatch(ids);
    }

    public List<Account> getByIds(List<Long> ids) {
        return account_repository.findAllById(ids);
    }

    public Account getAccount(Long id) {
        return account_repository.getReferenceById(id);
    }

    public Account edit(Long account_id, AccountDTO account_dto) {
        Account account = getAccount(account_id);
        if (account == null) {
            throw new IllegalStateException("Invalid account. Please try again");
        }

        Account new_account = new Account();
        ObjectMapper.mapAll(account_dto, new_account);
        if (!Objects.equals(account.getCode(), new_account.getCode()) && isExist(new_account)) {
            throw new IllegalStateException("Account code has been already existed. Please try again");
        }
        account.setName(new_account.getName());
        account.setPhone(new_account.getPhone());
        account.setCode(new_account.getCode());
        account.setGender(new_account.getGender());
        account.setEmail(new_account.getEmail());
        account.setAssignedUserId(new_account.getAssignedUserId());
        account.setBirthday(new_account.getBirthday());
        account.setJob(new_account.getJob());
        account.setSourceId(new_account.getSourceId());
        account.setReferrerId(new_account.getReferrerId());
        account.setRelationshipId(new_account.getRelationshipId());

        try {
            AccountValidator validator = new AccountValidator(account, this);
            validator.validate();
            account.setLastUpdate(Timer.now());
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }

        return account_repository.save(account);
    }
}
