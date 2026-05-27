package com.no.ai.api.city.dto;

import com.no.ai.api.city.domain.Status;
import com.no.ai.api.city.domain.TrafficEvent;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class TrafficEventDto {

    @Getter
    @Builder
    public static class Response {
        private String spotNum;
        private int hour;
        private Double riskScore;
        private String message;
        private Status status;
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
