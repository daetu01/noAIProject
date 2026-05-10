package com.no.ai.inventory.domain;

import com.no.ai.item.domain.Item;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Inventory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InventoryItem> inventoryItems = new ArrayList<>();
}
