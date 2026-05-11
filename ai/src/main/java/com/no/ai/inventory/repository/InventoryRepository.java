package com.no.ai.inventory.repository;

import com.no.ai.inventory.domain.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
}
