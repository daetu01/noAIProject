package com.no.ai.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class InventoryDto {

    @Getter
    @AllArgsConstructor
    public static class GET {
        private Long itemId;
        private String itemName;
        private int quantity;
        private String description;
    }
}
