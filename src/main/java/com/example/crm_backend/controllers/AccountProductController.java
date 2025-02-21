package com.example.crm_backend.controllers;

import com.example.crm_backend.dtos.account.AccountProductDTO;
import com.example.crm_backend.entities.account.product.AccountProduct;
import com.example.crm_backend.entities.user.User;
import com.example.crm_backend.services.account.AccountProductService;
import com.example.crm_backend.services.UserService;
import com.example.crm_backend.utils.SessionHelper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(path = "/api/account/product")
public class AccountProductController {
    private final UserService user_service;

    private final AccountProductService account_product_service;

    @Autowired
    public AccountProductController(UserService user_service, AccountProductService account_product_service) {
        this.user_service = user_service;
        this.account_product_service = account_product_service;
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
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "You do not have access"));
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
            return ResponseEntity.ok(Map.of("account_product", ap.release(current_user)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
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
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "You do not have access"));
        }

        try {
            AccountProduct edited_ap = account_product_service.edit(id, dto);
            return ResponseEntity.ok(Map.of("account_product", edited_ap.release(current_user)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
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
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "You do not have access"));
        }

        try {
            account_product_service.delete(id);
            return ResponseEntity.ok(Map.of("message", "Delete successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/duplicate/{id}")
    public ResponseEntity<Object> duplicateAccountProduct(@PathVariable("id") Long id, HttpServletRequest request) {
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        try {
            AccountProduct ap = account_product_service.duplicate(id, current_user);
            return ResponseEntity.ok(Map.of("account_product", ap.release(current_user)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/list/{account_id}")
    public ResponseEntity<Object> search(@RequestParam String query, @RequestParam Long start, @RequestParam Long end, @RequestParam Long page, @RequestParam Long ipp, HttpServletRequest request, @PathVariable String account_id){
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        Page<AccountProduct> aps = account_product_service.paginate(Math.toIntExact(ipp), Math.toIntExact(page), account_id, query, start, end);
        Page<AccountProductDTO> data = aps.map(ap -> ap.release(current_user));

        return ResponseEntity.ok(data);
    }

}
