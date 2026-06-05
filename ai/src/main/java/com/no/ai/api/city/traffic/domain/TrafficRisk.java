package com.no.ai.api.city.traffic.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "spot_num",
                                "ymd",
                                "hour"
                        }
                )
        }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrafficRisk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    private String spotNum;

    private LocalDate ymd;

    private Integer hour;

    private Double currentVolume;

    private Double averageVolume;

    private Double riskScore;

    private Boolean aiAnomaly;
    private Double aiScore;
    private String aiStatus;

    @Enumerated(EnumType.STRING)
    private Status status;

    public void update(
            Double currentVolume,
            Double averageVolume,
            Double riskScore,
            Status status,
            Boolean aiAnomaly,
            Double aiScore,
            String aiStatus

    ){
        this.currentVolume = currentVolume;
        this.averageVolume = averageVolume;
        this.riskScore = riskScore;
        this.status = status;
        this.aiAnomaly = aiAnomaly;
        this.aiScore = aiScore;
        this.aiStatus = aiStatus;
    }

}
