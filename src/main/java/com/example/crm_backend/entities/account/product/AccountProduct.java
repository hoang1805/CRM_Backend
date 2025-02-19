package com.example.crm_backend.entities.account.product;

import com.example.crm_backend.dtos.AccountProductDTO;
import com.example.crm_backend.entities.Releasable;
import com.example.crm_backend.entities.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Entity
@Table(name = "AccountProducts")
@Getter
@Setter
public class AccountProduct implements Releasable<AccountProductDTO> {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    private String name;

    private String description;

    private Long quantity;

    private Float price;

    private Float discount;

    private Float tax;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "creator_id")
    private Long creatorId;

    @Column(name = "created_at")
    private Long createdAt;

    @Column(name = "last_update")
    private Long lastUpdate;

    @Transient
    private AccountProductACL acl;

    public AccountProduct() {}

    public AccountProduct(Long id, String name, String description, Long quantity, Float price, Float discount, Float tax, Long accountId, Long creatorId, Long createdAt, Long lastUpdate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
        this.discount = discount;
        this.tax = tax;
        this.accountId = accountId;
        this.creatorId = creatorId;
        this.createdAt = createdAt;
        this.lastUpdate = lastUpdate;
    }

    public AccountProduct(String name, String description, Long quantity, Float price, Float discount, Float tax, Long accountId, Long creatorId, Long createdAt, Long lastUpdate) {
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
        this.discount = discount;
        this.tax = tax;
        this.accountId = accountId;
        this.creatorId = creatorId;
        this.createdAt = createdAt;
        this.lastUpdate = lastUpdate;
    }

    public float getTotal() {
        float total = this.price * this.quantity;
        float discount = total * this.discount / 100;
        total -= discount;
        float tax = total * this.tax / 100;
        return total + tax;
    }

    public AccountProductACL acl() {
        if (this.acl == null) {
            this.acl = new AccountProductACL(this);
        }
        return acl;
    }

    @Override
    public AccountProductDTO release(User session_user) {
        AccountProductDTO dto = new AccountProductDTO();
        dto.setId(id).setName(name).setDescription(description).setQuantity(quantity)
                .setPrice(price).setDiscount(discount).setTax(tax).setTotal(getTotal())
                .setAccountId(accountId).setCreatorId(creatorId).setCreatedAt(createdAt)
                .setLastUpdate(lastUpdate);
        if (session_user != null) {
            dto.setAcl(Map.of(
                    "view", this.acl().canView(session_user),
                    "edit", this.acl().canEdit(session_user),
                    "delete", this.acl().canDelete(session_user)
            ));
        }
        return dto;
    }

    @Override
    public AccountProductDTO release() {
        return release(null);
    }

    @Override
    public AccountProductDTO releaseCompact(User session_user) {
        return release(session_user);
    }

    @Override
    public AccountProductDTO releaseCompact() {
        return releaseCompact(null);
    }
}
