package com.example.crm_backend.services.account;

import com.example.crm_backend.dtos.account.AccountProductDTO;
import com.example.crm_backend.entities.account.product.AccountProduct;
import com.example.crm_backend.entities.account.product.AccountProductValidator;
import com.example.crm_backend.entities.user.User;
import com.example.crm_backend.enums.Role;
import com.example.crm_backend.repositories.AccountProductRepository;
import com.example.crm_backend.repositories.AccountRepository;
import com.example.crm_backend.utils.ObjectMapper;
import com.example.crm_backend.utils.Timer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

import static org.springframework.data.domain.Sort.Direction.DESC;

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
        ap.setSystemId(user.getSystemId());

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

        ap.setCategory(dto.getCategory());

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

    public AccountProduct duplicate(Long id, User user) {
        AccountProduct ap = getById(id);
        if (ap == null) {
            throw new IllegalStateException("Invalid account product. Please try again");
        }

        AccountProduct duplicate = new AccountProduct();

        duplicate.setName(ap.getName());
        duplicate.setDescription(ap.getDescription());
        duplicate.setCategory(ap.getCategory());
        duplicate.setPrice(ap.getPrice());
        duplicate.setQuantity(ap.getQuantity());
        duplicate.setDiscount(ap.getDiscount());
        duplicate.setTax(ap.getTax());
        duplicate.setCreatorId(user.getId());
        duplicate.setAccountId(ap.getAccountId());
        duplicate.setLastUpdate(Timer.now());
        duplicate.setCreatedAt(Timer.now());

        return account_product_repository.save(duplicate);
    }


    public Page<AccountProduct> paginate(User user, int ipp, int page, String account_id, String query, Long start, Long end) {
        Pageable request = PageRequest.of(page, ipp, Sort.by(DESC, "id"));
        if (end != null && end != 0) {
            end = Timer.endOfDay(end);
        }

        if (user.getRole() == Role.SUPER_ADMIN) {
            return account_product_repository.searchProducts(account_id, query, start, end, request);
        }

        return account_product_repository.searchProducts(account_id, query, start, end, user.getSystemId(), request);

//        System.out.println(String.format("ipp: %d, page: %d, account_id: %s, query: %s, start: %d, end: %d", ipp, page, account_id, query, start, end));

    }

    public Double getTotal(Long account_id) {
        List<AccountProduct> list = account_product_repository.findByAccountId(account_id);
        Double total = 0.0;
        for (AccountProduct ap : list) {
            total += ap.getTotal();
        }

        return total;
    }

    public Long countProducts(Long account_id) {
        if (account_id == null) {
            return 0L;
        }

        return (long) account_product_repository.findByAccountId(account_id).size();
    }

    public Long lastBought(Long account_id) {
        if (account_id == null) {
            return 0L;
        }

        List<AccountProduct> list = account_product_repository.findByAccountId(account_id);
        list.sort(Comparator.comparing(AccountProduct::getLastUpdate));

        if (list.isEmpty()) {
            return 0L;
        }

        return list.getLast().getLastUpdate();
    }
}
