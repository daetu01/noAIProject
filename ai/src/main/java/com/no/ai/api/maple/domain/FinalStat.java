package com.no.ai.api.maple.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FinalStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String statName;
    private String statValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_stat_id")
    private CharacterStat characterStat;
}