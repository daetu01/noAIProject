package com.no.ai.inventory.domain;

import com.no.ai.item.domain.Item;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Entity
@Builder
@Getter
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id")
    private Inventory inventory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private int quantity;

    public void addQuantity(int quantity) {
        this.quantity += quantity;
    }
}
