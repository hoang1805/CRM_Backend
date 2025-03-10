package com.example.crm_backend.controllers;

import com.example.crm_backend.dtos.account.AccountDTO;
import com.example.crm_backend.dtos.UserDTO;
import com.example.crm_backend.entities.account.Account;
import com.example.crm_backend.entities.user.User;
import com.example.crm_backend.enums.Role;
import com.example.crm_backend.services.NotificationService;
import com.example.crm_backend.services.account.AccountExporter;
import com.example.crm_backend.services.account.AccountImporter;
import com.example.crm_backend.services.account.AccountService;
import com.example.crm_backend.services.UserService;
import com.example.crm_backend.utils.SessionHelper;
import com.example.crm_backend.utils.excel.ExcelImporter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping(path = "api/account")
public class AccountController {
    private final AccountService account_service;

    private final UserService user_service;

    private final AccountImporter account_importer;

    private final AccountExporter account_exporter;

    private final NotificationService notification_service;

    @Autowired
    public AccountController(AccountService account_service, UserService user_service, AccountImporter accountImporter, AccountExporter accountExporter, NotificationService notificationService) {
        this.account_service = account_service;
        this.user_service = user_service;
        account_importer = accountImporter;
        account_exporter = accountExporter;
        notification_service = notificationService;
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
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("code", "FORBIDDEN","message", "You do not have access"));
        }

        return ResponseEntity.ok(Map.of("account", account.release(current_user)));
    }

    @GetMapping("/list")
    public ResponseEntity<Object> getAccounts(@RequestParam(defaultValue = "10") int ipp, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "") String query, @RequestParam(defaultValue = "0") int relationship_id, HttpServletRequest request){
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        Page<Account> accounts = null;
        if (Objects.equals(current_user.getRole(), Role.SUPER_ADMIN)) {
            accounts = account_service.paginate(ipp, page, query, relationship_id);
        } else {
            accounts = account_service.paginate(ipp, page, query, relationship_id, current_user.getSystemId());
//            accounts = account_service.paginate(ipp, page, query, relationship_id, current_user);
        }
        Page<AccountDTO> data = accounts.map(account -> account.release(current_user));

        return ResponseEntity.ok(facet(data, current_user));
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
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("code", "BAD_REQUEST", "message", e.getMessage()));
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
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("code", "FORBIDDEN", "message", "You do not have permission"));
        }

        try {
            Account edited = account_service.edit(id, account_DTO);
            notification_service.notify(current_user, "Account", edited.collectFollowers(), "${user} edited Account ${object_name} that you followed", edited.getName(), edited.getLink());

            return ResponseEntity.ok(Map.of("account", edited.release(current_user)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("code", "BAD_REQUEST", "message", e.getMessage()));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteAccount(@PathVariable Long id, HttpServletRequest request) {
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        Account account = account_service.getAccount(id);
        if (account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Account not found"));
        }

        if (!account.acl().canDelete(current_user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("code", "FORBIDDEN", "message", "You do not have permission"));
        }

        try {
            account_service.deleteAccount(id);
            notification_service.notify(current_user, "Account", List.of(account.getCreatorId(), account.getAssignedUserId(), account.getReferrerId()), "${user} Account deleted ${object_name} that you followed", account.getName());
            return ResponseEntity.ok("Xóa thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi xóa: " + e.getMessage());
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
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("code", "FORBIDDEN", "message", "You do not have permission"));
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

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String query, HttpServletRequest request) {
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        List<Account> accounts = account_service.searchAccounts(query, current_user);
        return ResponseEntity.ok(accounts.stream().map(Account::releaseCompact).collect(Collectors.toList()));
    }

    private Page<AccountDTO> facet(Page<AccountDTO> data, User current_user) {
//        List<Long> user_ids = data.stream().flatMap(account -> Stream.of(account.getAssignedUserId(), account.getCreatorId())).filter(Objects::nonNull).toList();
//        List<User> users = user_service.loadUsers(user_ids, current_user);
//        Map<Long, UserDTO> map = users.stream()
//                .collect(Collectors.toMap(User::getId, User::releaseCompact));

        return data.map(account -> {
//            account.setCreatorExport(map.get(account.getCreatorId()));
//            account.setUserExport(map.get(account.getAssignedUserId()));
            return account;
        });
    }

    @PostMapping(value = "/import/upload.file", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<Object> uploadFile(
            @RequestPart("file") MultipartFile file,
            @RequestPart("options") String optionsJson, // Đổi Map -> String
            HttpServletRequest request
    ) {
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid user"));
        }

        if (current_user.getRole() != Role.ADMIN && current_user.getRole() != Role.MANAGER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("code", "FORBIDDEN", "message", "You do not have permission"));
        }

        if (!ExcelImporter.isExcelFile(file)) {
            return ResponseEntity.badRequest().body("File không đúng định dạng Excel");
        }

        // Parse JSON string thành Map
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> options;
        try {
            options = objectMapper.readValue(optionsJson, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid options format"));
        }

        Map<String, Boolean> refined_options = new HashMap<>();
        refined_options.put("ignore_error", Boolean.parseBoolean(options.getOrDefault("ignore_error", "false").toString()));
        refined_options.put("allow_override", Boolean.parseBoolean(options.getOrDefault("allow_override", "false").toString()));

        try {
            List<AccountDTO> data = account_importer.readFile(file, refined_options, current_user.getSystemId());
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("code", "BAD_REQUEST", "message", e.getMessage()));
        }
    }


    @GetMapping("/import/template")
    public ResponseEntity<Resource> getTemplateFile(HttpServletRequest request) {
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body((Resource) Map.of("message", "Invalid user"));
        }

        try {
            ByteArrayResource resource = account_importer.generateTemplate();

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=template.xlsx")
                    .contentLength(resource.contentLength())
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(resource);
        } catch (Exception e) {
//            throw new RuntimeException(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/import/save", consumes = "multipart/form-data")
    public ResponseEntity<Object> importAccounts(
            @RequestPart("options") String optionsJson,
            @RequestPart("accounts") String accountsJson,
            HttpServletRequest request
    ) {
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body((Resource) Map.of("message", "Invalid user"));
        }
        ObjectMapper objectMapper = new ObjectMapper();

        // Convert options JSON -> Map<String, Object>
        Map<String, Object> options;
        try {
            options = objectMapper.readValue(optionsJson, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid options format"));
        }

        Map<String, Boolean> refined_options = new HashMap<>();
        refined_options.put("ignore_error", Boolean.parseBoolean(options.getOrDefault("ignore_error", "false").toString()));
        refined_options.put("allow_override", Boolean.parseBoolean(options.getOrDefault("allow_override", "false").toString()));

        // Convert accounts JSON -> List<AccountDTO>
        List<AccountDTO> accounts;
        try {
            accounts = objectMapper.readValue(accountsJson, new TypeReference<List<AccountDTO>>() {});
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid accounts format"));
        }

        try {
            int success = account_service.importAccounts(accounts, current_user, refined_options.get("ignore_error"), refined_options.get("allow_override"));
            return ResponseEntity.ok(Map.of("success", success));
        } catch (Exception e) {
//            throw e;
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("code", "BAD_REQUEST", "message", e.getMessage()));
        }
    }

    @PostMapping("/export")
    public ResponseEntity<Resource> exportAccounts(@RequestBody List<Long> account_ids, HttpServletRequest request) {
        User current_user = SessionHelper.getSessionUser(request, user_service);
        if (current_user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body((Resource) Map.of("message", "Invalid user"));
        }

        try {
            ByteArrayResource resource = account_exporter.exportAccounts(account_ids, current_user.getSystemId());

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=template.xlsx")
                    .contentLength(resource.contentLength())
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(resource);
        } catch (Exception e) {
//            throw new RuntimeException(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
