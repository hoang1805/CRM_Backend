package com.example.crm_backend.repositories;

import com.example.crm_backend.entities.account.product.AccountProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountProductRepository extends JpaRepository<AccountProduct, Long> {
}
