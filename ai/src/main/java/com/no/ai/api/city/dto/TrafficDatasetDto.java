package com.no.ai.api.city.dto;

import com.no.ai.api.city.domain.TrafficVolume;
import lombok.Builder;
import lombok.Getter;

public class TrafficDatasetDto {

    @Getter
    @Builder
    public static class Response {
        private String spotNum;
        private Integer hour;
        private Integer inVolume;
        private Integer outVolume;
        private Integer totalVolume;
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
