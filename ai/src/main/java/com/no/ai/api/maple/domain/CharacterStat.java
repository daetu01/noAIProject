package com.no.ai.api.maple.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CharacterStat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "character_info_id")
    private CharacterInfo characterInfo;

    private String characterClass;

    @OneToMany(mappedBy = "characterStat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FinalStat> finalStats = new ArrayList<>();

    private int remainAp;

}


