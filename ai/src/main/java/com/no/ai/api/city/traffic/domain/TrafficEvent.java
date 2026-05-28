package com.no.ai.api.city.traffic.domain;

import jakarta.persistence.*;
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
    @Column(columnDefinition = "TEXT")
    private String message;
    private LocalDateTime recordedAt;

    public static TrafficEvent create(
            String spotNum,
            int hour,
            Double riskRate,
            String message,
            Status status
    ){
        TrafficEvent event = new TrafficEvent();

        event.spotNum = spotNum;
        event.hour = hour;
        event.riskRate = riskRate;
        event.message = message;
        event.status = status;
        event.recordedAt = LocalDateTime.now();
        return event;
    }

    public void updateMessage(
            String message
    ) {
        this.message = message;
    }

    public void update(
            Double riskRate,
            String message,
            Status status
    ) {
        this.riskRate = riskRate;
        this.message = message;
        this.status = status;
        this.recordedAt = LocalDateTime.now();
    }
}
