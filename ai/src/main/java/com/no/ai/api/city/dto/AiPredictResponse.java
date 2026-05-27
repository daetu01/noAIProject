package com.no.ai.api.city.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AiPredictResponse {
    private Boolean anomaly;
    private Double aiScore;
    private String status;
}
