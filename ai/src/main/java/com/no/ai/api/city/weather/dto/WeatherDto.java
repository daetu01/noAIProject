package com.no.ai.api.city.weather.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class WeatherDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WeatherRow {

        // 날짜
        private String ymd;

        // 지점번호
        private String stnId;

        // 위도
        private Double lat;

        // 경도
        private Double lon;

        // 해발고도
        private Double altd;

        // 평균기온
        private Double avgTemp;

        // 최고기온
        private Double maxTemp;

        // 최고기온 발생시각
        private String maxTempOccurTime;

        // 최저기온
        private Double minTemp;

        // 최저기온 발생시각
        private String minTempOccurTime;

        // 아침최저기온
        private Double morningMinTemp;

        // 아침최저기온 발생시각
        private String morningMinTempOccurTime;

        // 낮최고기온
        private Double daytimeMaxTemp;

        // 낮최고기온 발생시각
        private String daytimeMaxTempOccurTime;

        // 밤최저기온
        private Double nightMinTemp;

        // 밤최저기온 발생시각
        private String nightMinTempOccurTime;

        public static WeatherRow from(
                String[] tokens
        ) {

            return WeatherRow.builder()
                    .ymd(tokens[0])
                    .stnId(tokens[1])
                    .lat(toDouble(tokens[2]))
                    .lon(toDouble(tokens[3]))
                    .altd(toDouble(tokens[4]))
                    .avgTemp(toDouble(tokens[5]))
                    .maxTemp(toDouble(tokens[6]))
                    .maxTempOccurTime(tokens[7])
                    .minTemp(toDouble(tokens[8]))
                    .minTempOccurTime(tokens[9])
                    .morningMinTemp(toDouble(tokens[10]))
                    .morningMinTempOccurTime(tokens[11])
                    .daytimeMaxTemp(toDouble(tokens[12]))
                    .daytimeMaxTempOccurTime(tokens[13])
                    .nightMinTemp(toDouble(tokens[14]))
                    .nightMinTempOccurTime(tokens[15])
                    .build();
        }

        private static Double toDouble(String value) {
            if (value == null ||
                    value.isBlank() ||
                    "-".equals(value.trim())) {
                return null;
            }

            return Double.parseDouble(value.trim());
        }
    }
}
