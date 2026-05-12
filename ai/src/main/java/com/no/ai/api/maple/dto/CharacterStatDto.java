package com.no.ai.api.maple.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.no.ai.api.maple.domain.CharacterInfo;
import com.no.ai.api.maple.domain.FinalStat;
import lombok.*;

import java.util.List;


public class CharacterStatDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class GET {
        @JsonProperty("character_class")
        private String characterClass;

        @JsonProperty("final_stat")
        private List<FinalStatDto> finalStat;

        @JsonProperty("remain_ap")
        private int remainAp;
    }

    @Getter
    @Setter
    public static class FinalStatDto {

        @JsonProperty("stat_name")
        private String statName;

        @JsonProperty("stat_value")
        private String statValue;
    }

}

