package com.no.ai.api.city.domain;

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
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
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
