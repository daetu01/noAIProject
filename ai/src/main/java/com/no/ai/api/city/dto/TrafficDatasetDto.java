package com.no.ai.api.city.dto;

import com.no.ai.api.city.domain.TrafficVolume;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

public class TrafficDatasetDto {

    @Getter
    @Builder
    @Schema(name = "TrafficDatasetResponse", description = "AI 학습/분석용 교통 데이터셋 응답")
    public static class Response {
        @Schema(description = "교통 지점 번호", example = "A-01")
        private String spotNum;

        @Schema(description = "시간(0~23)", example = "9")
        private Integer hour;

        @Schema(description = "진입 교통량", example = "1240")
        private Integer inVolume;

        @Schema(description = "진출 교통량", example = "1180")
        private Integer outVolume;

        @Schema(description = "총 교통량", example = "2420")
        private Integer totalVolume;

        @Schema(description = "요일. 월요일=1, 일요일=7", example = "3")
        private Integer dayOfWeek;

        public static Response from (
                TrafficVolume volume
        ) {
            return Response.builder()
                    .spotNum(
                            volume.getSpotNum()
                    )
                    .hour(
                            volume.getHour()
                    )
                    .inVolume(
                            volume.getInVolume()
                    )
                    .outVolume(
                            volume.getOutVolume()
                    )
                    .totalVolume(
                            volume.getTotalVolume()
                    )
                    .dayOfWeek(
                            volume.getYmd()
                                    .getDayOfWeek()
                                    .getValue()
                    )
                    .build();
        }
    }
}
