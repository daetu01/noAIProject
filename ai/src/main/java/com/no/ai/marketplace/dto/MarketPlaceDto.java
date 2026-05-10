package com.no.ai.marketplace.dto;

import com.no.ai.item.domain.Item;
import com.no.ai.item.dto.ItemDto;
import lombok.Getter;

public class MarketPlaceDto {

    @Getter
    public static class CREATE {
        private String name;
        private Long price;
    }
}
