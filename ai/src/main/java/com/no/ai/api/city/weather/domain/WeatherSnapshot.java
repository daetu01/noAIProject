package com.no.ai.api.city.weather.domain;

import com.no.ai.api.city.weather.dto.WeatherDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                            "ymd",
                                "stn_id"
                        }
                )
        }
)
public class WeatherSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    private LocalDate ymd;

    @Column(name = "stn_id")
    private String stnId;

    private Double lat;

    private Double lon;

    private Double avgTemp;

    private Double maxTemp;

    private Double minTemp;

    private Double morningMinTemp;

    private Double daytimeMaxTemp;

    private Double nightMinTemp;

    public void updateFrom(
            WeatherDto.WeatherRow row
    ) {
        this.lat = row.getLat();
        this.lon = row.getLon();
        this.avgTemp = row.getAvgTemp();
        this.maxTemp = row.getMaxTemp();
        this.minTemp = row.getMinTemp();
        this.morningMinTemp = row.getMorningMinTemp();
        this.daytimeMaxTemp = row.getDaytimeMaxTemp();
        this.nightMinTemp = row.getNightMinTemp();
    }

    public static WeatherSnapshot from(
            WeatherDto.WeatherRow row
    ) {

        return WeatherSnapshot.builder()
                .ymd(
                        LocalDate.parse(
                                row.getYmd(),
                                DateTimeFormatter.BASIC_ISO_DATE
                        )
                )
                .stnId(
                        row.getStnId()
                )
                .lat(
                        row.getLat()
                )
                .lon(
                        row.getLon()
                )
                .avgTemp(
                        row.getAvgTemp()
                )
                .maxTemp(
                        row.getMaxTemp()
                )
                .minTemp(
                        row.getMinTemp()
                )
                .morningMinTemp(
                        row.getMorningMinTemp()
                )
                .daytimeMaxTemp(
                        row.getDaytimeMaxTemp()
                )
                .nightMinTemp(
                        row.getNightMinTemp()
                )
                .build();
    }

}
