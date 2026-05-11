package com.no.ai.inventory.repository;

import com.no.ai.inventory.domain.InventoryItem;
import com.no.ai.item.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    Optional<InventoryItem> findByItem(Item item);

}


