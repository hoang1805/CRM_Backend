package com.example.crm_backend.services;

import com.example.crm_backend.entities.account.Account;
import com.example.crm_backend.entities.relationship.Relationship;
import com.example.crm_backend.entities.source.Source;
import com.example.crm_backend.entities.user.User;
import com.example.crm_backend.repositories.AccountRepository;
import com.example.crm_backend.repositories.RelationshipRepository;
import com.example.crm_backend.repositories.SourceRepository;
import com.example.crm_backend.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class SearchEngine {
    private final AccountRepository account_repository;

    private final UserRepository user_repository;

    private final SourceRepository source_repository;

    private final RelationshipRepository relationship_repository;

    private final Map<String, List<?>> cache;

    public SearchEngine(AccountRepository account_repository, UserRepository userRepository, SourceRepository sourceRepository, RelationshipRepository relationshipRepository) {
        this.account_repository = account_repository;
        user_repository = userRepository;
        source_repository = sourceRepository;
        relationship_repository = relationshipRepository;
        cache = new HashMap<>();
    }

    public List<?> get(String key) {
        return cache.get(key);
    }

    public void add(String key, List<?> list) {
        cache.put(key, list);
    }

    public boolean isExist(String key) {
        return cache.containsKey(key);
    }

    public void remove(String key) {
        if (!isExist(key)) {
            return;
        }

        cache.remove(key);
    }

    public boolean equalsIgnoreCase(String a, String b) {
        return a.equalsIgnoreCase(b);
    }

    public Account searchAccount(String query, long systemId) {
        if (!isExist("accounts")) {
            add("accounts", account_repository.findAll());
        }

        List<Account> accounts = (List<Account>) get("accounts");
        for (Account account : accounts) {
            if (!Objects.equals(account.getSystemId(), systemId)) {
                continue;
            }

            if (account.getCode().equals(query)) {
                return account;
            }

            try {
                Long id = Long.parseLong(query);
                if (account.getId().equals(id)) {
                    return account;
                }
            } catch (Exception _) {
            }
        }

        return null;
    }

    public User searchUser(String query, Long systemId) {
        if (!isExist("users")) {
            add("users", user_repository.findAll());
        }

        List<User> users = (List<User>) get("users");
        for (User user : users) {
            if (!Objects.equals(user.getSystemId(), systemId)) {
                continue;
            }

            if (user.getUsername().equals(query)) {
                return user;
            }

            try {
                Long id = Long.parseLong(query);
                if (user.getId().equals(id)) {
                    return user;
                }
            } catch (Exception _) {
            }
        }

        for (User user : users) {
            if (!Objects.equals(user.getSystemId(), systemId)) {
                continue;
            }

            if (equalsIgnoreCase(user.getName(), query)) {
                return user;
            }
        }

        return null;
    }

    public Source searchSource(String query, Long systemId) {
        if (!isExist("sources")) {
            add("sources", source_repository.findAll());
        }

        List<Source> sources = (List<Source>) get("sources");
        for (Source source : sources) {
            if (!Objects.equals(source.getSystemId(), systemId)) {
                continue;
            }

            if (source.getCode().equals(query)) {
                return source;
            }
        }
        return null;
    }

    public Source searchSourceById(String query, Long systemId) {
        if (!isExist("sources")) {
            add("sources", source_repository.findAll());
        }

        List<Source> sources = (List<Source>) get("sources");
        for (Source source : sources) {
            if (!Objects.equals(source.getSystemId(), systemId)) {
                continue;
            }

            if (source.getId().equals(Long.parseLong(query))) {
                return source;
            }
        }

        return null;
    }

    public Relationship searchRelationship(String query, Long systemId) {
        if (!isExist("relationships")) {
            add("relationships", relationship_repository.findAll());
        }

        List<Relationship> relationships = (List<Relationship>) get("relationships");
        for (Relationship relationship : relationships) {
            if (!Objects.equals(relationship.getSystemId(), systemId)) {
                continue;
            }

            if (relationship.getName().equals(query)) {
                return relationship;
            }
        }

        for (Relationship relationship : relationships) {
            if (!Objects.equals(relationship.getSystemId(), systemId)) {
                continue;
            }

            if (equalsIgnoreCase(relationship.getName(), query)) {
                return relationship;
            }
        }

        return null;
    }

    public Relationship searchRelationshipById(String query, Long systemId) {
        if (!isExist("relationships")) {
            add("relationships", relationship_repository.findAll());
        }

        if (query == null || query.isEmpty()) {
            return null;
        }

        List<Relationship> relationships = (List<Relationship>) get("relationships");
        for (Relationship relationship : relationships) {
            if (!Objects.equals(relationship.getSystemId(), systemId)) {
                continue;
            }

            if (relationship.getId().equals(Long.parseLong(query))) {
                return relationship;
            }
        }

        return null;
    }
}
