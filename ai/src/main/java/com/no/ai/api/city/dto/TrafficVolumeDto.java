package com.no.ai.api.city.dto;

import com.no.ai.api.city.domain.Status;
import com.no.ai.api.city.domain.TrafficVolume;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

public class TrafficVolumeDto {

    @Builder
    @Getter
    @Schema(name = "TrafficVolumeResponse", description = "교통 지점의 특정 시간 교통량 응답")
    public static class Response {

        @Schema(description = "교통 지점 번호", example = "A-01")
        private String spotNum;

        @Schema(description = "교통량 기준 날짜", example = "2026-05-27")
        private LocalDate ymd;

        @Schema(description = "교통량 기준 시간(0~23)", example = "9")
        private Integer hour;

        @Schema(description = "진입 교통량", example = "1240")
        private Integer inVolume;

        @Schema(description = "진출 교통량", example = "1180")
        private Integer outVolume;

        @Schema(description = "총 교통량", example = "2420")
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
    @Schema(name = "TrafficHourVolumeResponse", description = "특정 지점의 시간대별 교통량 응답")
    public static class VolumeResponse {
        @Schema(description = "시간(0~23)", example = "9")
        private int hour;

        @Schema(description = "총 교통량", example = "2420")
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
    @Schema(name = "TrafficRiskResponse", description = "특정 지점의 위험도 응답")
    public static class RiskResponse {
        @Schema(description = "교통 지점 번호", example = "A-01")
        private String spotNum;

        @Schema(description = "위험도 기준 시간(0~23)", example = "9")
        private int hour;

        @Schema(description = "현재 총 교통량", example = "2420.0")
        private Double currentVolume;

        @Schema(description = "해당 지점 평균 교통량", example = "1600.5")
        private Double averageVolume;

        @Schema(description = "위험 점수. 평균 대비 현재 교통량 비율 기반", example = "151.2")
        private Double riskScore;

        @Schema(description = "교통 위험 상태", example = "WARNING", allowableValues = {"NORMAL", "WARNING", "DANGER"})
        private Status status;
    }

    @Builder
    @Getter
    @Schema(name = "TrafficRiskMapResponse", description = "지도 표시용 전체 지점 위험도 응답")
    public static class RiskResponses {
        @Schema(description = "교통 지점 번호", example = "A-01")
        private String spotNum;

        @Schema(description = "교통 지점명", example = "성산로")
        private String spotName;

        @Schema(description = "위험 점수", example = "151.2")
        private Double riskScore;

        @Schema(description = "교통 위험 상태", example = "WARNING", allowableValues = {"NORMAL", "WARNING", "DANGER"})
        private Status status;

        @Schema(description = "서울시 TM X 좌표", example = "194321.25")
        private Double tmX;

        @Schema(description = "서울시 TM Y 좌표", example = "452112.77")
        private Double tmY;
    }

    @Builder
    @Getter
    @Schema(name = "TrafficDetailResponse", description = "교통 지점 상세 응답")
    public static class Detail {
        @Schema(description = "교통 지점명", example = "성산로")
        private String spotName;

        @Schema(description = "교통 지점 번호", example = "A-01")
        private String spotNum;

        @Schema(description = "위험 점수", example = "151.2")
        private Double riskScore;

        @Schema(description = "교통 위험 상태", example = "WARNING", allowableValues = {"NORMAL", "WARNING", "DANGER"})
        private Status status;

        @Schema(description = "해당 날짜의 시간대별 교통량")
        private List<TrafficVolumeDto.VolumeResponse> todayVolumes;

        // AI 분석 결과
        @Schema(description = "AI 이상 패턴 탐지 여부", example = "true")
        private Boolean aiAnomaly;

        @Schema(description = "AI 이상 탐지 점수", example = "-0.0421")
        private Double aiScore;

        @Schema(description = "AI 분석 상태 메시지", example = "ANOMALY")
        private String aiStatus;

        // 위험 원인 메시지
        @Schema(description = "위험 원인 추정 메시지", example = "위험 : 151.2\n원인 추정:\n- 사고 : 성산로 추돌 사고")
        private String message;
    }
}

