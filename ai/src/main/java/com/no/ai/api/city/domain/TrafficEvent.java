package com.no.ai.api.city.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class TrafficEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    private String spotNum;

    private int hour;

    private Double riskRate;
    private Status status;
    private LocalDateTime recordedAt;
}
