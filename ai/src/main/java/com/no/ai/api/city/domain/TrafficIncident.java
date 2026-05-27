package com.no.ai.api.city.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "acc_id"
                        }
                )
        }
)
@Builder
public class TrafficIncident {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;
    private String accId;

    private LocalDateTime occurTime;

    private LocalDateTime expectedClearTime;

    private String accType;

    private String accDType;

    private String linkId;

    private Double grs80tmX;

    private Double grs80tmY;


    @Column(length = 1000)
    private String accInfo;


    public static TrafficIncident create(
            String accId,
            LocalDateTime occurTime,
            LocalDateTime expectedClearTime,
            String accType,
            String accDType,
            String linkId,
            Double grs80tmX,
            Double grs80tmY,
            String accInfo
    ) {

        return TrafficIncident.builder()
                .accId(accId)
                .occurTime(occurTime)
                .expectedClearTime(expectedClearTime)
                .accType(accType)
                .accDType(accDType)
                .linkId(linkId)
                .grs80tmX(grs80tmX)
                .grs80tmY(grs80tmY)
                .accInfo(accInfo)
                .build();
    }

    public void update(
            String accInfo,
            LocalDateTime expectedClearTime
    ){
        this.accInfo = accInfo;
        this.expectedClearTime =
                expectedClearTime;
    }
}
