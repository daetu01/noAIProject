package com.no.ai.item.dto;

import lombok.Getter;

public class ItemDto {

    @Getter
    public static class CREATE {
        private String name;
        private int eqLevel;
        private String description;
    }
}
