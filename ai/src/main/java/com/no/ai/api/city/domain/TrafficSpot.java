package com.no.ai.api.city.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class TrafficSpot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String spotNum;
    private String spotName;

    private Double tmX;
    private Double tmY;

    private Double latitude;
    private Double longitude;
}
