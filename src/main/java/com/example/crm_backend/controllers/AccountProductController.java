package com.example.crm_backend.controllers;

import com.example.crm_backend.dtos.account.AccountProductDTO;
import com.example.crm_backend.entities.account.Account;
import com.example.crm_backend.entities.account.product.AccountProduct;
import com.example.crm_backend.entities.user.User;
import com.example.crm_backend.services.NotificationService;
import com.example.crm_backend.services.account.AccountProductService;
import com.example.crm_backend.services.UserService;
import com.example.crm_backend.services.account.AccountService;
import com.example.crm_backend.utils.SessionHelper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/account/product")
public class AccountProductController {
    private final UserService user_service;

    private final AccountProductService account_product_service;

    private final NotificationService notification_service;

    private final AccountService account_service;

    @Autowired
    public AccountProductController(UserService user_service, AccountProductService account_product_service, NotificationService notificationService, AccountService accountService) {
        this.user_service = user_service;
        this.account_product_service = account_product_service;
        notification_service = notificationService;
        account_service = accountService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getAccountProduct(@PathVariable("id") Long id, HttpServletRequest request) {
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        AccountProduct ap = account_product_service.getById(id);
        if (ap == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Account product not found"));
        }

        if (!ap.acl().canView(current_user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("code", "FORBIDDEN", "message", "You do not have permission"));
        }

        return ResponseEntity.ok(Map.of("account_product", ap.release(current_user)));
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createAccountProduct(@RequestBody AccountProductDTO dto, HttpServletRequest request) {
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        try {
            AccountProduct ap = account_product_service.create(dto, current_user);
            Account account = account_service.getAccount(ap.getAccountId());
            notification_service.notify(current_user, "Product", List.of(ap.getCreatorId(), account.getCreatorId(), account.getAssignedUserId(), account.getReferrerId()), "${user} created Product ${object_name} that you followed", ap.getName(), ap.getLink());

            return ResponseEntity.ok(Map.of("account_product", ap.release(current_user)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("code", "BAD_REQUEST", "message", e.getMessage()));
        }
    }

    @PostMapping("/edit/{id}")
    public ResponseEntity<Object> editAccountProduct(@PathVariable("id") Long id, @RequestBody AccountProductDTO dto, HttpServletRequest request) {
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        AccountProduct ap = account_product_service.getById(id);
        if (ap == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Account product not found"));
        }

        if (!ap.acl().canEdit(current_user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("code", "FORBIDDEN", "message", "You do not have permission"));
        }

        try {
            AccountProduct edited_ap = account_product_service.edit(id, dto);
            Account account = account_service.getAccount(edited_ap.getAccountId());
            notification_service.notify(current_user, "Product", List.of(edited_ap.getCreatorId(), account.getCreatorId(), account.getAssignedUserId(), account.getReferrerId()), "${user} edited Product ${object_name} that you followed", edited_ap.getName(), edited_ap.getLink());
            return ResponseEntity.ok(Map.of("account_product", edited_ap.release(current_user)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("code", "BAD_REQUEST", "message", e.getMessage()));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteAccountProduct(@PathVariable("id") Long id, HttpServletRequest request) {
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        AccountProduct ap = account_product_service.getById(id);
        if (ap == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Account product not found"));
        }

        if (!ap.acl().canDelete(current_user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("code", "FORBIDDEN", "message", "You do not have permission"));
        }

        try {
            account_product_service.delete(id);
            Account account = account_service.getAccount(ap.getAccountId());
            notification_service.notify(current_user, "Product", List.of(ap.getCreatorId(), account.getCreatorId(), account.getAssignedUserId(), account.getReferrerId()), "${user} deleted Product ${object_name} that you followed", ap.getName());

            return ResponseEntity.ok(Map.of("message", "Delete successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("code", "BAD_REQUEST", "message", e.getMessage()));
        }
    }

    @PostMapping("/duplicate/{id}")
    public ResponseEntity<Object> duplicateAccountProduct(@PathVariable("id") Long id, HttpServletRequest request) {
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        AccountProduct ap = account_product_service.getById(id);
        if (ap == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Account product not found"));
        }

        if (!ap.acl().canEdit(current_user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("code", "FORBIDDEN", "message", "You do not have permission"));
        }

        try {
            AccountProduct nap = account_product_service.duplicate(id, current_user);
            return ResponseEntity.ok(Map.of("account_product", nap.release(current_user)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("code", "BAD_REQUEST", "message", e.getMessage()));
        }
    }

    @GetMapping("/list/{account_id}")
    public ResponseEntity<Object> search(@RequestParam String query, @RequestParam Long start, @RequestParam Long end, @RequestParam Long page, @RequestParam Long ipp, HttpServletRequest request, @PathVariable String account_id){
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        Page<AccountProduct> aps = account_product_service.paginate(current_user, Math.toIntExact(ipp), Math.toIntExact(page), account_id, query, start, end);
        Page<AccountProductDTO> data = aps.map(ap -> ap.release(current_user));

        return ResponseEntity.ok(data);
    }

}
