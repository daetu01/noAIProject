package com.no.ai.api.maple.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;

public class CharacterInfoDto {
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GET {
        private String date;

        @JsonProperty("character_name")
        private String characterName;

        @JsonProperty("world_name")
        private String worldName;

        @JsonProperty("character_gender")
        private String characterGender;

        @JsonProperty("character_class")
        private String characterClass;

        @JsonProperty("character_class_level")
        private String characterClassLevel;

        @JsonProperty("character_level")
        private Integer characterLevel;

        @JsonProperty("character_exp")
        private Long characterExp;

        @JsonProperty("character_exp_rate")
        private String characterExpRate;

        @JsonProperty("character_guild_name")
        private String characterGuildName;

        @JsonProperty("character_image")
        private String characterImage;

        @JsonProperty("character_date_create")
        private OffsetDateTime characterDateCreate;

        @JsonProperty("access_flag")
        private String accessFlag;

        @JsonProperty("liberation_quest_clear")
        private String liberationQuestClear;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DETAIL {
        private String date;

        @JsonProperty("character_name")
        private String characterName;

        @JsonProperty("world_name")
        private String worldName;

        @JsonProperty("character_gender")
        private String characterGender;

        @JsonProperty("character_class")
        private String characterClass;

        @JsonProperty("character_class_level")
        private String characterClassLevel;

        @JsonProperty("character_level")
        private Integer characterLevel;

        @JsonProperty("character_exp")
        private Long characterExp;

        @JsonProperty("character_exp_rate")
        private String characterExpRate;

        @JsonProperty("character_guild_name")
        private String characterGuildName;

        @JsonProperty("character_image")
        private String characterImage;

        @JsonProperty("character_date_create")
        private OffsetDateTime characterDateCreate;

        @JsonProperty("access_flag")
        private String accessFlag;

        @JsonProperty("liberation_quest_clear")
        private String liberationQuestClear;

        private int popularity;

        @JsonProperty("final_stat")
        private List<CharacterStatDto.FinalStatDto> finalStat;

        @JsonProperty("remain_ap")
        private int remainAp;
    }
}
