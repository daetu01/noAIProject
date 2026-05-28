package com.no.ai.api.city.traffic.domain;

import jakarta.persistence.*;
import lombok.*;

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
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TrafficVolume {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "spot_num")
    private String spotNum;
    @Column(name = "ymd")
    private LocalDate ymd;
    @Column(name = "hour")
    private Integer hour;

    private Integer inVolume;

    private Integer outVolume;

    private Integer totalVolume;
}
