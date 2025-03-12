package com.example.crm_backend.services.account;

import com.example.crm_backend.dtos.account.AccountDTO;
import com.example.crm_backend.entities.account.Account;
import com.example.crm_backend.entities.account.AccountValidator;
import com.example.crm_backend.entities.account.product.AccountProduct;
import com.example.crm_backend.entities.user.User;
import com.example.crm_backend.enums.Role;
import com.example.crm_backend.events.AccountEvent;
import com.example.crm_backend.repositories.AccountRepository;
import com.example.crm_backend.repositories.UserRepository;
import com.example.crm_backend.services.FeedbackService;
import com.example.crm_backend.services.SearchEngine;
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

import java.util.*;

@Service
public class AccountService {
    private final AccountRepository account_repository;

    private final UserRepository user_repository;

    private final SearchEngine search_engine;

    private final FeedbackService feedback_service;

    private final AccountProductService account_product_service;

    private final ApplicationEventPublisher event_publisher;

    @Autowired
    public AccountService(AccountRepository account_repository, UserRepository user_repository, SearchEngine searchEngine, FeedbackService feedbackService, AccountProductService accountProductService, ApplicationEventPublisher eventPublisher) {
        this.account_repository = account_repository;
        this.user_repository = user_repository;
        search_engine = searchEngine;
        feedback_service = feedbackService;
        account_product_service = accountProductService;
        event_publisher = eventPublisher;
    }

    public SearchEngine getSearchEngine() {
        return search_engine;
    }

    public Page<Account> paginate(int ipp, int page, String query, int relationship_id ){
        Pageable request = PageRequest.of(page, ipp, Sort.by(Sort.Direction.DESC, "id"));

        return account_repository.searchAccounts(query, (long) relationship_id, request);
    }

    public Page<Account> paginate(int ipp, int page, String query, int relationship_id, Long system_id) {
        Pageable request = PageRequest.of(page, ipp, Sort.by(Sort.Direction.DESC, "id"));

        return account_repository.searchAccounts(query, (long) relationship_id, system_id, request);
    }

    public List<Account> searchAccounts(String query, User user) {
        return account_repository.searchAccounts(query, user.getSystemId(), 20L);
    }

    @Transactional
    public Account createAccount(AccountDTO data, User creator) {
        Account account = new Account();
        ObjectMapper.mapAll(data, account);
        AccountValidator validator = new AccountValidator(account, this);
        validator.validate();

        account.setCreatorId(creator.getId());
        account.setLastUpdate(Timer.now());
        account.setCreatedAt(Timer.now());
        account.setSystemId(creator.getSystemId());

        account = account_repository.save(account);
        event_publisher.publishEvent(AccountEvent.created(account, this));
        return account;
    }

    public boolean isExist(Account account) {
        String code = account.getCode();
        Long system_id = account.getSystemId();
        return !code.isEmpty() && account_repository.existsByCodeAndSystemId(code, system_id);
    }

    public Long count(User user) {
        if (user.getRole() == Role.SUPER_ADMIN) {
            return account_repository.count();
        }
        return account_repository.countBySystemId(user.getSystemId());
    }

    public boolean isValidUser(Long user_id) {
        if (user_id == null) {
            return false;
        }
        return user_repository.existsById(user_id);
    }

    @Transactional
    public void deleteAccounts(List<Long> ids) {
        List<Account> accounts = account_repository.findAllById(ids);
        account_repository.deleteAllByIdInBatch(ids);
        for (Account account : accounts) {
            event_publisher.publishEvent(AccountEvent.deleted(account, this));
        }

    }

    public List<Account> getByIds(List<Long> ids) {
        return account_repository.findAllById(ids);
    }

    public Account getAccount(Long id) {
        return account_repository.getReferenceById(id);
    }

    @Transactional
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

        account = account_repository.save(account);
        event_publisher.publishEvent(AccountEvent.edited(account, this));
        return account;
    }

    @Transactional
    public void deleteAccount(Long id) {
        Account account = getAccount(id);
        if (account == null) {
            return;
        }

        account_repository.deleteById(id);
        event_publisher.publishEvent(AccountEvent.deleted(account, this));
    }

    public List<Account> loadAccounts(List<Long> account_ids) {
        account_ids = account_ids.stream().distinct().collect(java.util.stream.Collectors.toList());
        return account_repository.findAllById(account_ids);
    }

    @Transactional
    public int importAccounts(List<AccountDTO> dtos, User user, boolean ignore_error, boolean override) {
        List<Account> accounts = new ArrayList<>();
        for (AccountDTO dto : dtos) {
            Account account = search_engine.searchAccount(dto.getCode(), user.getSystemId());
            if (account == null) {
                try {
                    Account new_account = new Account();
                    ObjectMapper.mapAll(dto, new_account);
                    AccountValidator validator = new AccountValidator(new_account, this);
                    validator.validate();
                    new_account.setLastUpdate(Timer.now());
                    new_account.setCreatedAt(Timer.now());
                    new_account.setCreatorId(user.getId());
                    new_account.setSystemId(user.getSystemId());

                    accounts.add(new_account);
                } catch (Exception e) {
                    if (!ignore_error) {
                        throw new IllegalStateException(e.getMessage());
                    }
                }

                continue;
            }

            if (!override && !ignore_error) {
                throw new IllegalStateException("Account with code: " + dto.getCode() + " already exists");
            }

            if (!override) {
                continue;
            }

            Account new_account = new Account();
            ObjectMapper.mapAll(dto, new_account);

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
            account.setSystemId(user.getSystemId());

            try {
                AccountValidator validator = new AccountValidator(account, this);
                validator.validate();
                account.setLastUpdate(Timer.now());
                accounts.add(account);
            } catch (Exception e) {
                if (!ignore_error) {
                    throw new IllegalStateException(e.getMessage());
                }
            }
        }

        List<Account> refine_accounts = new ArrayList<>();
        Map<String, Boolean> mp = new HashMap<>();
        for (Account account : accounts) {
            if (account == null) {
                continue;
            }

            if (account.getCode() == null) {
                continue;
            }

            if (!mp.containsKey(account.getCode())) {
                mp.put(account.getCode(), true);
                refine_accounts.add(account);
            }
        }

        try {
            int success = account_repository.saveAll(refine_accounts).size();
            event_publisher.publishEvent(AccountEvent.imported(this));
            return success;
        } catch (Exception e) {
            if (!ignore_error) {
                throw new IllegalStateException(e.getMessage());
            }
        }

        return 0;
    }

    public Long getLastContact(Long account_id) {
        if (account_id == null) {
            return 0L;
        }

        return feedback_service.getLastByAccount(account_id);
    }

    public Long countContacts(Long account_id) {
        if (account_id == null) {
            return 0L;
        }

        return feedback_service.countContact(account_id);
    }

    public Double getTotalValue(Long account_id) {
        if (account_id == null) {
            return 0.0;
        }

        return account_product_service.getTotal(account_id);
    }
}
