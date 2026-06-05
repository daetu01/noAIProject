package com.no.ai.api.city.traffic.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "spot_num",
                                "spot_name",
                                "tmX",
                                "tmY"
                        }
                )
        }
)
public class TrafficSpot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "spot_num")
    private String spotNum;
    @Column(name = "spot_name")
    private String spotName;

    @Column(name = "tmX")
    private Double tmX;
    @Column(name = "tmY")
    private Double tmY;

    public void update(
            String spotName,
            Double tmX,
            Double tmY
    ){
        this.spotName = spotName;
        this.tmX = tmX;
        this.tmY = tmY;
    }
}
