package com.no.ai.api.city.weather.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@NoArgsConstructor
public class ForecastDto {

    private static final Pattern FORECAST_PATTERN =
            Pattern.compile(
                    "^(\\S+)\\s+" +   // REG_ID
                            "(\\S+)\\s+" +     // TM_FC
                            "(\\S+)\\s+" +     // TM_EF
                            "(\\S+)\\s+" +     // MOD
                            "(\\S+)\\s+" +     // NE
                            "(\\S+)\\s+" +     // STN
                            "(\\S+)\\s+" +     // C
                            "(\\S+)\\s+" +     // MAN_ID
                            "(\\S+)\\s+" +     // MAN_FC
                            "(\\S+)\\s+" +     // W1
                            "(\\S+)\\s+" +     // T
                            "(\\S+)\\s+" +     // W2
                            "(\\S+)\\s+" +     // TA
                            "(\\S+)\\s+" +     // ST
                            "(\\S+)\\s+" +     // SKY
                            "(\\S+)\\s+" +     // PREP
                            "\"?(.+?)\"?$"     // WF
            );

    @Builder
    @Getter
    public static class Response {
        private String regId;
        private String tmFc;
        private String tmEf;
        private String mod;
        private Integer ne;
        private String stn;
        private String c;
        private String manId;
        private String manFc;

        private String windDirectionStart;
        private String windTrend;
        private String windDirectionEnd;

        private Integer temperature;
        private Integer rainProbability;
        private String skyCode;
        private Integer precipitationCode;
        private String weatherText;

        public static ForecastDto.Response from(String line) {
            Matcher matcher = FORECAST_PATTERN.matcher(line.trim());

            if (!matcher.matches()) {
                throw new IllegalArgumentException("예보 파싱 실패: " + line);
            }

            return ForecastDto.Response.builder()
                    .regId(matcher.group(1))
                    .tmFc(matcher.group(2))
                    .tmEf(matcher.group(3))
                    .mod(matcher.group(4))
                    .ne(toInteger(matcher.group(5)))
                    .stn(matcher.group(6))
                    .c(matcher.group(7))
                    .manId(matcher.group(8))
                    .manFc(matcher.group(9))
                    .windDirectionStart(matcher.group(10))
                    .windTrend(matcher.group(11))
                    .windDirectionEnd(matcher.group(12))
                    .temperature(toInteger(matcher.group(13)))
                    .rainProbability(toInteger(matcher.group(14)))
                    .skyCode(matcher.group(15))
                    .precipitationCode(toInteger(matcher.group(16)))
                    .weatherText(matcher.group(17))
                    .build();
        }

        private static Integer toInteger(String value) {
            if (value == null ||
                    value.isBlank() ||
                    "-".equals(value.trim())) {
                return null;
            }

            return Integer.parseInt(value.trim());
        }
    }
}
