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

    public List<?> getData(String key) {
        return cache.get(key);
    }

    public void addData(String key, List<?> list) {
        cache.put(key, list);
    }

    public boolean isExist(String key) {
        return cache.containsKey(key);
    }

    public boolean equalsIgnoreCase(String a, String b) {
        return a.equalsIgnoreCase(b);
    }

    public Account searchAccount(String query, long systemId) {
        if (!isExist("accounts")) {
            addData("accounts", account_repository.findAll());
        }

        List<Account> accounts = (List<Account>) getData("accounts");
        for (Account account : accounts) {
            if (account.getSystemId() == systemId) {

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
        }

        return null;
    }

    public User searchUser(String query, long systemId) {
        if (!isExist("users")) {
            addData("users", user_repository.findAll());
        }

        List<User> users = (List<User>) getData("users");
        for (User user : users) {
            if (user.getSystemId() == systemId) {
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
        }

        for (User user : users) {
            if (user.getSystemId() == systemId) {
                if (equalsIgnoreCase(user.getName(), query)) {
                    return user;
                }
            }
        }

        return null;
    }

    public Source searchSource(String query, long systemId) {
        if (!isExist("sources")) {
            addData("sources", source_repository.findAll());
        }

        List<Source> sources = (List<Source>) getData("sources");
        for (Source source : sources) {
            if (source.getSystemId() == systemId) {
                if (source.getCode().equals(query)) {
                    return source;
                }
            }
        }
        return null;
    }

    public Source searchSourceById(String query, long systemId) {
        if (!isExist("sources")) {
            addData("sources", source_repository.findAll());
        }

        List<Source> sources = (List<Source>) getData("sources");
        for (Source source : sources) {
            if (source.getSystemId() == systemId) {
                if (source.getId().equals(Long.parseLong(query))) {
                    return source;
                }
            }
        }

        return null;
    }

    public Relationship searchRelationship(String query, long systemId) {
        if (!isExist("relationships")) {
            addData("relationships", relationship_repository.findAll());
        }

        List<Relationship> relationships = (List<Relationship>) getData("relationships");
        for (Relationship relationship : relationships) {
            if (relationship.getSystemId() == systemId) {
                if (relationship.getName().equals(query)) {
                    return relationship;
                }
            }
        }

        for (Relationship relationship : relationships) {
            if (relationship.getSystemId() == systemId) {
                if (equalsIgnoreCase(relationship.getName(), query)) {
                    return relationship;
                }
            }
        }

        return null;
    }

    public Relationship searchRelationshipById(String query, long systemId) {
        if (!isExist("relationships")) {
            addData("relationships", relationship_repository.findAll());
        }

        List<Relationship> relationships = (List<Relationship>) getData("relationships");
        for (Relationship relationship : relationships) {
            if (relationship.getSystemId() == systemId) {
                if (relationship.getId().equals(Long.parseLong(query))) {
                    return relationship;
                }
            }
        }

        return null;
    }
}
