package com.no.ai.api.city.traffic.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(name = "TrafficAlertMessage", description = "웹소켓 교통 위험 알림 메시지")
public class TrafficAlertDto {
    @Schema(description = "교통 지점 번호", example = "A-01")
    private String spotNum;

    @Schema(description = "이벤트 기준 시간(0~23)", example = "9")
    private Integer hour;

    @Schema(description = "위험 점수", example = "151.2")
    private Double riskScore;

    @Schema(description = "교통 위험 상태", example = "WARNING")
    private String status;

    @Schema(description = "위험 원인 추정 메시지", example = "위험 : 151.2\n원인 추정:\n- 사고 : 성산로 추돌 사고")
    private String message;
}
