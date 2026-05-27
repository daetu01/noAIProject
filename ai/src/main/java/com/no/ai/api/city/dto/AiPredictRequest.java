package com.no.ai.api.city.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AiPredictRequest {
    private Integer hour;
    private Integer inVolume;
    private Integer outVolume;
    private Integer totalVolume;
    private Integer dayOfWeek;
}
