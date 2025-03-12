package com.example.crm_backend.services.account;

import com.example.crm_backend.entities.account.Account;
import com.example.crm_backend.entities.relationship.Relationship;
import com.example.crm_backend.entities.source.Source;
import com.example.crm_backend.entities.user.User;
import com.example.crm_backend.repositories.AccountRepository;
import com.example.crm_backend.services.SearchEngine;
import com.example.crm_backend.utils.excel.ExcelExporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AccountExporter {
    private final List<String> account_columns = List.of("name", "phone", "code", "gender", "email", "assigned_user", "birthday", "job", "source", "referrer", "relationship");;

    private final List<String> columns = List.of(
            "Tên khách hàng *", "Số điện thoại", "Mã khách hàng *", "Giới tính",
            "Email *", "Người phụ trách", "Ngày sinh", "Ngành nghề",
            "Nguồn khách hàng *", "Người giới thiệu *", "Mối quan hệ"
    );

    private final SearchEngine search_engine;

    private final AccountRepository account_repository;

    @Autowired
    public AccountExporter(SearchEngine searchEngine, AccountRepository accountRepository) {
        search_engine = searchEngine;
        account_repository = accountRepository;
    }

    private List<Object> export(Account account, long systemId) {
        List<Object> row = new ArrayList<>();
        row.add(account.getName());
        row.add(account.getPhone());

        row.add(account.getCode());
        row.add(account.getGender());

        row.add(account.getEmail());

        Long assigned_user_id = account.getAssignedUserId();
        User user = search_engine.searchUser(String.valueOf(assigned_user_id), systemId);
        if (user != null) {
            row.add(user.getUsername());
        } else {
            row.add(null);
        }

        if (account.getBirthday() != null) {
            Date date = new Date(account.getBirthday());
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            row.add(formatter.format(date));
        } else {
            row.add(null);
        }

        row.add(account.getJob());

        Source source = search_engine.searchSourceById(String.valueOf(account.getSourceId()), systemId);
        if (source != null) {
            row.add(source.getCode());
        } else {
            row.add(null);
        }

        User referrer = search_engine.searchUser(String.valueOf(account.getReferrerId()), systemId);
        if (referrer != null) {
            row.add(referrer.getUsername());
        } else {
            row.add(null);
        }

        String relationship_id = null;
        if (account.getRelationshipId() != null) {
            relationship_id = String.valueOf(account.getRelationshipId());
        }
        Relationship relationship = search_engine.searchRelationshipById(relationship_id, systemId);
        if (relationship != null) {
            row.add(relationship.getName());
        } else {
            row.add(null);
        }

        return row;
    }

    public ByteArrayResource exportAccounts(List<Long> account_ids, long systemId) {
        List<Account> accounts = account_repository.findAllById(account_ids);
        List<List<Object>> data = new ArrayList<>();
        for (Account account : accounts) {
            data.add(export(account, systemId));
        }

        return ExcelExporter.generateFile(columns, data);
    }
}
