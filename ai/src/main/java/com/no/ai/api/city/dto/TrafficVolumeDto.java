package com.no.ai.api.city.dto;

import com.no.ai.api.city.domain.Status;
import com.no.ai.api.city.domain.TrafficVolume;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

public class TrafficVolumeDto {

    @Builder
    @Getter
    public static class Response {

        private String spotNum;

        private LocalDate ymd;

        private Integer hour;

        private Integer inVolume;

        private Integer outVolume;

        private Integer totalVolume;

        public static Response from(
                TrafficVolume trafficVolume
        ){
            return Response.builder()
                    .spotNum(
                            trafficVolume.getSpotNum()
                    )
                    .ymd(
                            trafficVolume.getYmd()
                    )
                    .hour(
                            trafficVolume.getHour()
                    )
                    .inVolume(
                            trafficVolume.getInVolume()
                    )
                    .outVolume(
                            trafficVolume.getOutVolume()
                    )
                    .totalVolume(
                            trafficVolume.getTotalVolume()
                    )
                    .build();
        }
    }

    @Getter
    @Builder
    public static class VolumeResponse {
        private int hour;
        private int totalVolume;

        public static TrafficVolumeDto.VolumeResponse from(
                TrafficVolume trafficVolume
        ) {
            return VolumeResponse.builder()
                    .hour(trafficVolume.getHour())
                    .totalVolume(trafficVolume.getTotalVolume())
                    .build();
        }

    }

    @Builder
    @Getter
    public static class RiskResponse {
        private String spotNum;
        private int hour;
        private Double currentVolume;
        private Double averageVolume;
        private Double riskScore;
        private Status status;
    }

    @Builder
    @Getter
    public static class RiskResponses {
        private String spotNum;
        private String spotName;
        private Double riskScore;
        private Status status;
        private Double tmX;
        private Double tmY;
    }

    @Builder
    @Getter
    public static class Detail {
        private String spotName;
        private String spotNum;
        private Double riskScore;
        private Status status;
        private List<TrafficVolumeDto.VolumeResponse> todayVolumes;
    }
}

