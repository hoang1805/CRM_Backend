package com.example.crm_backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
public class AccountProductDTO {
    @JsonProperty("id")
    private Long id;

    private String name;

    private String category;

    private String description;

    @JsonProperty("quantity")
    private Long quantity;

    @JsonProperty("price")
    private Float price;

    @JsonProperty("discount")
    private Float discount;

    @JsonProperty("tax")
    private Float tax;

    private Double total;

    @JsonProperty("account_id")
    private Long accountId;

    @JsonProperty("creator_id")
    private Long creatorId;

    @JsonProperty("created_at")
    private Long createdAt;

    @JsonProperty("last_update")
    private Long lastUpdate;

    private Map<String, Boolean> acl = new HashMap<>();

    public AccountProductDTO() {
    }

    public AccountProductDTO(Long id, String name, String category, String description, Long quantity, Float price, Float discount, Float tax, Long accountId, Long creatorId, Long createdAt, Long lastUpdate) {
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
    }
}
