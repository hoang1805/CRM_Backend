package com.example.crm_backend.entities.account.product;

import com.example.crm_backend.dtos.account.AccountProductDTO;
import com.example.crm_backend.entities.HasLink;
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
public class AccountProduct implements Releasable<AccountProductDTO>, HasLink {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    private String name;

    private String category;

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

    @Column(name = "system_id")
    private Long systemId;

    @Transient
    private AccountProductACL acl;

    public AccountProduct() {}

    public AccountProduct(Long id, String name, String category, String description, Long quantity, Float price, Float discount, Float tax, Long accountId, Long creatorId, Long createdAt, Long lastUpdate, Long systemId) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
        this.discount = discount;
        this.tax = tax;
        this.accountId = accountId;
        this.creatorId = creatorId;
        this.createdAt = createdAt;
        this.lastUpdate = lastUpdate;
        this.systemId = systemId;
    }

    public AccountProduct(String name, String category, String description, Long quantity, Float price, Float discount, Float tax, Long accountId, Long creatorId, Long createdAt, Long lastUpdate, Long systemId) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.quantity = quantity;
        this.price = price;
        this.discount = discount;
        this.tax = tax;
        this.accountId = accountId;
        this.creatorId = creatorId;
        this.createdAt = createdAt;
        this.lastUpdate = lastUpdate;
        this.systemId = systemId;
    }

    public double getTotal() {
        double price = 1.0 * this.price;
        double quantity = 1.0 * this.quantity;
        double d_discount = 1.0 * this.discount;
        double d_tax = 1.0 * this.tax;
        double total = 1.0 * price * quantity;
        double discount = 1.0 * total * d_discount / 100.0;
        total -= discount;
        double tax = 1.0 * total * d_tax / 100.0;
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
        dto.setId(id).setName(name).setCategory(category).setDescription(description).setQuantity(quantity)
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

    @Override
    public String getLink() {
        return "/account/" + accountId + "?tab=product";
    }
}
