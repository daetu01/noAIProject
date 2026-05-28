package com.no.ai.api.city.traffic.dto;

import com.no.ai.api.city.traffic.domain.Status;
import com.no.ai.api.city.traffic.domain.TrafficEvent;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class TrafficEventDto {

    @Getter
    @Builder
    @Schema(name = "TrafficEventResponse", description = "교통 위험 이벤트 응답")
    public static class Response {
        @Schema(description = "교통 지점 번호", example = "A-01")
        private String spotNum;

        @Schema(description = "이벤트 기준 시간(0~23)", example = "9")
        private int hour;

        @Schema(description = "위험 점수", example = "151.2")
        private Double riskScore;

        @Schema(description = "위험 원인 추정 메시지", example = "위험 : 151.2\n원인 추정:\n- 사고 : 성산로 추돌 사고")
        private String message;

        @Schema(description = "교통 위험 상태", example = "WARNING", allowableValues = {"NORMAL", "WARNING", "DANGER"})
        private Status status;

        @Schema(description = "이벤트 기록 시각", example = "2026-05-27T09:30:00")
        private LocalDateTime recordedAt;

        public static Response from(
                TrafficEvent event
        ) {
            return Response.builder()
                    .spotNum(
                            event.getSpotNum()
                    )
                    .riskScore(
                            event.getRiskRate()
                    )
                    .hour(
                            event.getHour()
                    )
                    .status(
                            event.getStatus()
                    )
                    .message(
                            event.getMessage()
                    )
                    .status(
                            event.getStatus()
                    )
                    .recordedAt(
                            event.getRecordedAt()
                    )
                    .build();
        }
    }
}
