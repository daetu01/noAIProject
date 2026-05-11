package com.no.ai.marketplace.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class MarketPlaceDto {

    @Getter
    public static class CREATE {
        private Long id;
        private String name;
        private Long price;
    }

    @Getter
    public static class UPDATE {
        private Long id;
        private String name;
        private Long price;
    }

    @Getter
    @AllArgsConstructor
    public static class GET {
        private Long id;
        private String itemName;
        private Long price;
        private String sellerNickName;
    }
}
