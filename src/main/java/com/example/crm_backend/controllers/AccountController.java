package com.example.crm_backend.controllers;

import com.example.crm_backend.dtos.AccountDTO;
import com.example.crm_backend.dtos.UserDTO;
import com.example.crm_backend.entities.account.Account;
import com.example.crm_backend.entities.user.User;
import com.example.crm_backend.enums.Role;
import com.example.crm_backend.services.AccountService;
import com.example.crm_backend.services.UserService;
import com.example.crm_backend.utils.SessionHelper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping(path = "api/account")
public class AccountController {
    private final AccountService account_service;

    private final UserService user_service;

    @Autowired
    public AccountController(AccountService account_service, UserService user_service) {
        this.account_service = account_service;
        this.user_service = user_service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getAccount(@PathVariable("id") Long id, HttpServletRequest request) {
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        Account account = account_service.getAccount(id);
        if (account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Account not found"));
        }

        if (!account.acl().canView(current_user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "You do not have access"));
        }

        return ResponseEntity.ok(Map.of("account", account.release(current_user)));
    }

    @GetMapping("/list")
    public ResponseEntity<Object> getAccounts(@RequestParam(defaultValue = "10") int ipp, @RequestParam(defaultValue = "0") int page, HttpServletRequest request){
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        Page<Account> accounts = null;
        if (Objects.equals(current_user.getRole(), Role.ADMIN)) {
            accounts = account_service.paginate(ipp, page);
        } else {
            accounts = account_service.paginate(ipp, page, current_user);
        }
        Page<AccountDTO> data = accounts.map(account -> account.release(current_user));

        return ResponseEntity.ok(facet(data));
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createAccount(@RequestBody AccountDTO accountDTO, HttpServletRequest request){
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        try {
            Account account = account_service.createAccount(accountDTO, current_user);
            return ResponseEntity.ok(Map.of("account", account.release(current_user)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/edit/{id}")
    public ResponseEntity<Object> editAccount(@RequestBody AccountDTO account_DTO, @PathVariable Long id, HttpServletRequest request) {
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        Account account = account_service.getAccount(id);
        if (account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Account not found"));
        }

        if(!account.acl().canEdit(current_user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "You do not have permission"));
        }

        try {
            Account edited = account_service.edit(id, account_DTO);
            return ResponseEntity.ok(Map.of("account", edited.release(current_user)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/delete.many")
    public ResponseEntity<Object> deleteAccounts(@RequestBody List<Long> ids, HttpServletRequest request) {
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        List<Account> accounts = account_service.getByIds(ids);
        for (Account account: accounts) {
            if (!account.acl().canDelete(current_user)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "You do not have permission"));
            }
        }


        try {
            account_service.deleteAccounts(ids);
            return ResponseEntity.ok("Xóa thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi xóa: " + e.getMessage());
        }
    }

    private Page<AccountDTO> facet(Page<AccountDTO> data) {
        List<Long> user_ids = data.stream().flatMap(account -> Stream.of(account.getAssignedUserId(), account.getCreatorId())).filter(Objects::nonNull).toList();
        List<User> users = user_service.loadUsers(user_ids);
        Map<Long, UserDTO> map = users.stream()
                .collect(Collectors.toMap(User::getId, User::releaseCompact));

        return data.map(account -> {
            account.setCreatorExport(map.get(account.getCreatorId()));
            account.setUserExport(map.get(account.getAssignedUserId()));
            return account;
        });
    }
}
