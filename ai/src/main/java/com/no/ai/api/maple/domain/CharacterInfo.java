package com.no.ai.api.maple.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@ToString
public class CharacterInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String ocidCode;

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
    @Column(columnDefinition = "TEXT")
    private String characterImage;

    @JsonProperty("character_date_create")
    private OffsetDateTime characterDateCreate;

    @JsonProperty("access_flag")
    private String accessFlag;

    @JsonProperty("liberation_quest_clear")
    private String liberationQuestClear;

    private int popularity;
}
