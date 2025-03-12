package com.example.crm_backend.services.account;

import com.example.crm_backend.dtos.account.AccountDTO;
import com.example.crm_backend.entities.relationship.Relationship;
import com.example.crm_backend.entities.source.Source;
import com.example.crm_backend.entities.user.User;
import com.example.crm_backend.enums.Gender;
import com.example.crm_backend.services.SearchEngine;
import com.example.crm_backend.utils.Importer;
import com.example.crm_backend.utils.excel.ExcelImporter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AccountImporter {
    private final List<String> account_columns = List.of("name", "phone", "code", "gender", "email", "assigned_user", "birthday", "job", "source", "referrer", "relationship");;

    private final SearchEngine search_engine;

    private final int LIMIT = 5000;

    @Autowired
    public AccountImporter(SearchEngine search_engine) {
        this.search_engine = search_engine;
    }

    public List<AccountDTO> readFile(MultipartFile file, Map<String, Boolean> options, long systemId) {
        boolean ignore_error = options.getOrDefault("ignore_error", false);
        try {
            List<Map<String, String>> data = ExcelImporter.readFile(file.getInputStream(), account_columns, options, LIMIT);
            List<AccountDTO> accounts = new ArrayList<>();
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i) == null) {
                    continue;
                }

                if (this.isEmptyRow(data.get(i))) {
                    continue;
                }

                try {
                    accounts.add(readRow(data.get(i), systemId));
                } catch (Exception e) {
                    if (!ignore_error) {
                        throw new IllegalStateException("Có lỗi ở dòng " + (i + 1) + ": " + e.getMessage());
                    }
                }
            }

            return accounts;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    private boolean isEmptyRow(Map<String, String> row) {
        for (String column : account_columns) {
            String value = row.get(column);
            if (value == null || value.isEmpty()) {
                continue;
            }

            return false;
        }

        return true;
    }

    public AccountDTO readRow(Map<String, String> row, long systemId) {
        AccountDTO dto = new AccountDTO();

        readName(dto, row.get("name"));
        dto.setPhone(row.get("phone"));
        readCode(dto, row.get("code"));
        readGender(dto, row.get("gender"));
        readEmail(dto, row.get("email"));

        readAssignedUser(dto, row.get("assigned_user"), systemId);
        readBirthday(dto, row.get("birthday"));

        dto.setJob(row.get("job"));

        readSource(dto, row.get("source"), systemId);
        readReferrer(dto, row.get("referrer"), systemId);
        readRelationship(dto, row.get("relationship"), systemId);
        return dto;
    }

    private void readName(AccountDTO dto, String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalStateException("Name is empty: " + name);
        }
        dto.setName(name);
    }

    private void readCode(AccountDTO dto, String code) {
        if (code == null || code.isEmpty()) {
            throw new IllegalStateException("Code is empty");
        }
        dto.setCode(code);
    }

    private void readGender(AccountDTO dto, String gender) {
        if (gender == null || gender.isEmpty()) {
            dto.setGender(Gender.OTHER);
        }

        assert gender != null;
        if (gender.equalsIgnoreCase("male")) {
            dto.setGender(Gender.MALE);
            return ;
        }

        if (gender.equalsIgnoreCase("female")) {
            dto.setGender(Gender.FEMALE);
            return ;
        }

        if (gender.equalsIgnoreCase("other")) {
            dto.setGender(Gender.OTHER);
            return ;
        }

        if (gender.equalsIgnoreCase("nam")) {
            dto.setGender(Gender.MALE);
            return ;
        }

        if (gender.equalsIgnoreCase("nữ")) {
            dto.setGender(Gender.FEMALE);
            return ;
        }

        if (gender.equalsIgnoreCase("khác")) {
            dto.setGender(Gender.OTHER);
            return ;
        }

        if (gender.equals("1")) {
            dto.setGender(Gender.MALE);
            return ;
        }

        if (gender.equals("2")) {
            dto.setGender(Gender.FEMALE);
            return ;
        }

        if (gender.equals("0")) {
            dto.setGender(Gender.OTHER);
            return ;
        }



        try {
            dto.setGender(Gender.valueOf(gender));
        } catch (Exception _) {
            dto.setGender(Gender.OTHER);
        }
    }

    private void readEmail(AccountDTO dto, String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalStateException("Email is empty");
        }
        dto.setEmail(email);
    }

    private void readAssignedUser(AccountDTO dto, String assigned, long systemId) {
        if (assigned == null || assigned.isEmpty()) {
            return ;
        }

        User user = search_engine.searchUser(assigned, systemId);
        if (user == null) {
            throw new IllegalStateException("Invalid assigned user");
        }

        dto.setAssignedUserId(user.getId());
    }

    private void readBirthday(AccountDTO dto, String birthday) {
        if (birthday == null || birthday.isEmpty()) {
            return ;
        }

        dto.setBirthday(Importer.readDate(birthday));
    }

    private void readSource(AccountDTO dto, String source, long systemId) {
        if (source == null || source.isEmpty()) {
            throw new IllegalStateException("Source is empty");
        }

        Source result = search_engine.searchSource(source, systemId);
        if (result == null) {
            throw new IllegalStateException("Invalid source");
        }

        dto.setSourceId(result.getId());
    }

    private void readRelationship(AccountDTO dto, String relationship, long systemId) {
        if (relationship == null || relationship.isEmpty()) {
            return ;
        }

        Relationship res = search_engine.searchRelationship(relationship, systemId);
        if(res == null) {
            return ;
        }

        dto.setRelationshipId(res.getId());
    }

    private void readReferrer(AccountDTO dto, String referrer, long systemId) {
        if (referrer == null || referrer.isEmpty()) {
            throw new IllegalStateException("Referrer is empty");
        }

        User user = search_engine.searchUser(referrer, systemId);
        if (user == null) {
            throw new IllegalStateException("Invalid referrer");
        }

        dto.setReferrerId(user.getId());
    }

    public ByteArrayResource generateTemplate() {

        List<String> columns = List.of(
                "Tên khách hàng *", "Số điện thoại", "Mã khách hàng *", "Giới tính",
                "Email *", "Người phụ trách", "Ngày sinh", "Ngành nghề",
                "Nguồn khách hàng *", "Người giới thiệu *", "Mối quan hệ"
        );

        try {
            return ExcelImporter.generateTemplate(columns);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
