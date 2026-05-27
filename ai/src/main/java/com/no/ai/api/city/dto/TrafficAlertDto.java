package com.no.ai.api.city.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TrafficAlertDto {
    private String spotNum;
    private Integer hour;
    private Double riskScore;
    private String status;
    private String message;
}
