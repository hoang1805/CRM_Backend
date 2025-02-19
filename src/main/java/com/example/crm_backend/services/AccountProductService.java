package com.example.crm_backend.services;

import com.example.crm_backend.dtos.AccountProductDTO;
import com.example.crm_backend.entities.account.product.AccountProduct;
import com.example.crm_backend.entities.account.product.AccountProductValidator;
import com.example.crm_backend.entities.user.User;
import com.example.crm_backend.repositories.AccountProductRepository;
import com.example.crm_backend.repositories.AccountRepository;
import com.example.crm_backend.utils.ObjectMapper;
import com.example.crm_backend.utils.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountProductService {
    private final AccountRepository account_repository;

    private final AccountProductRepository account_product_repository;

    @Autowired
    public AccountProductService(AccountRepository account_repository, AccountProductRepository account_product_repository) {
        this.account_repository = account_repository;
        this.account_product_repository = account_product_repository;
    }

    public boolean isExistAccount(Long accountId) {
        return account_repository.existsById(accountId);
    }

    public AccountProduct getById(Long id) {
        return account_product_repository.getReferenceById(id);
    }

    public AccountProduct create(AccountProductDTO dto, User user) {
        AccountProduct ap = new AccountProduct();
        ObjectMapper.mapAll(dto, ap);

        //refine data
        if (ap.getDiscount() == null) {
            ap.setDiscount((float) 0);
        }

        if (ap.getQuantity() == null) {
            ap.setQuantity(0L);
        }

        if (ap.getPrice() == null) {
            ap.setPrice((float) 0);
        }

        if (ap.getTax() == null) {
            ap.setTax((float) 0);
        }

        AccountProductValidator validator = new AccountProductValidator(ap, this);
        validator.validate();

        ap.setCreatorId(user.getId());
        ap.setCreatedAt(Timer.now());
        ap.setLastUpdate(Timer.now());

        return account_product_repository.save(ap);
    }

    public AccountProduct edit(Long id, AccountProductDTO dto) {
        AccountProduct ap = getById(id);
        if (ap == null) {
            throw new IllegalStateException("Invalid account product. Please try again");
        }

        ap.setName(dto.getName());
        ap.setDescription(dto.getDescription());

        if (dto.getPrice() != null) {
            ap.setPrice(dto.getPrice());
        }

        if (dto.getQuantity() != null) {
            ap.setQuantity(dto.getQuantity());
        }

        if (dto.getDiscount() != null) {
            ap.setDiscount(dto.getDiscount());
        }

        if (dto.getTax() != null) {
            ap.setTax(dto.getTax());
        }

        try {
            AccountProductValidator validator = new AccountProductValidator(ap, this);
            validator.validate();
            ap.setLastUpdate(Timer.now());
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }

        return account_product_repository.save(ap);
    }

    public void delete(Long id) {
        if (id == null || !account_product_repository.existsById(id)) {
            throw new IllegalStateException("Invalid account product. Please try again");
        }

        account_product_repository.deleteById(id);
    }

    public AccountProduct duplicate(Long id) {
        AccountProduct ap = getById(id);
        if (ap == null) {
            throw new IllegalStateException("Invalid account product. Please try again");
        }

        ap.setId(null);
        ap.setLastUpdate(Timer.now());
        ap.setCreatedAt(Timer.now());

        return account_product_repository.save(ap);
    }
}
