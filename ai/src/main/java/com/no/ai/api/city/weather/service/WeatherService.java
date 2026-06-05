package com.no.ai.api.city.weather.service;

import com.no.ai.api.ApiConfig;
import com.no.ai.api.city.weather.domain.WeatherSnapshot;
import com.no.ai.api.city.weather.dto.ForecastDto;
import com.no.ai.api.city.weather.dto.WeatherDto;
import com.no.ai.api.city.weather.repository.ForecastRepository;
import com.no.ai.api.city.weather.repository.WeatherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class WeatherService {
    @Qualifier("weatherRestClient")
    private final RestClient weatherRestClient;
    private final ApiConfig apiConfig;

    private final ForecastRepository forecastRepository;
    private final WeatherRepository weatherRepository;

    public WeatherDto.WeatherRow dailyWeather(String ymd) {
        LocalDate date = LocalDate.parse(ymd, DateTimeFormatter.BASIC_ISO_DATE);

        return weatherRepository.findTopByYmdAndStnIdOrderByIdDesc(date, "108")
                .map(this::fromSnapshot)
                .orElseGet(() -> {
                    String response = fetchDailyWeather(ymd, "108");
                    log.info("기상청 응답={}", response);
                    WeatherDto.WeatherRow row = parseWeatherResponse(response);
                    weatherRepository.saveAndFlush(WeatherSnapshot.from(row));
                    return row;
                });
    }

    private WeatherDto.WeatherRow fromSnapshot(WeatherSnapshot s) {
        return WeatherDto.WeatherRow.builder()
                .ymd(s.getYmd().format(DateTimeFormatter.BASIC_ISO_DATE))
                .stnId(s.getStnId())
                .lat(s.getLat())
                .lon(s.getLon())
                .avgTemp(s.getAvgTemp())
                .maxTemp(s.getMaxTemp())
                .minTemp(s.getMinTemp())
                .morningMinTemp(s.getMorningMinTemp())
                .daytimeMaxTemp(s.getDaytimeMaxTemp())
                .nightMinTemp(s.getNightMinTemp())
                .build();
    }

    private String fetchDailyWeather(
            String ymd,
            String stnId
    ) {
        return weatherRestClient.get()
                .uri(uriBuilder ->
                        uriBuilder.path(
                                        "/api/typ01/url/sts_ta.php"
                                )
                                .queryParam(
                                        "tm1",
                                        ymd
                                )
                                .queryParam(
                                        "tm2",
                                        ymd
                                )
                                .queryParam(
                                        "stn_id",
                                        stnId
                                )
                                .queryParam(
                                        "help",
                                        "0"
                                )
                                .queryParam(
                                        "disp",
                                        "0"
                                )
                                .queryParam(
                                        "authKey",
                                        apiConfig.getWeather()
                                                .getApikey()
                                )
                                .build()
                )
                .retrieve()
                .body(
                        String.class
                );
    }

    private WeatherDto.WeatherRow parseWeatherResponse(
            String response
    ) {
        return Arrays.stream(response.split("\\R"))
                .map(String::trim)
                .filter(line -> !line.isBlank())
                .filter(line -> !line.startsWith("#"))
                .map(line -> line.replace("=", ""))
                .map(line -> line.split(","))
                .filter(tokens -> tokens.length >= 16)
                .map(WeatherDto.WeatherRow::from)
                .findFirst()
                .orElseThrow(
                        () -> new RuntimeException("날씨 데이터 없음")
                );
    }


    /**
     * 단기예보 갖고 오는 API 입니다.
     * @return
     */
    public List<ForecastDto.Response> forecastWeather() {
        try {
            String response = fetchForecastWeather();
            log.info("단기예보 : {}", response);
            return parseForecastText(response);
        } catch (Exception e) {
            log.warn("단기예보 API 호출 실패: {}", e.getMessage());
            return List.of();
        }
    }

    public List<ForecastDto.Response> parseForecastText(String response) {
        return Arrays.stream(response.split("\\R"))
                .map(String::trim)
                .filter(line -> !line.isBlank())
                .filter(line -> !line.startsWith("#"))
                .map(this::parseForecastLine)
                .filter(Objects::nonNull)
                .toList();
    }

    private ForecastDto.Response parseForecastLine(String line) {
        try {
            return ForecastDto.Response.from(line);
        } catch (Exception e) {
            log.debug("단기예보 라인 파싱 스킵: line={}, reason={}", line, e.getMessage());
            return null;
        }
    }

    public String fetchForecastWeather() {
        return weatherRestClient.get()
                .uri(uriBuilder ->
                        uriBuilder.path(
                                        "/api/typ01/url/fct_afs_dl.php"
                                )
                                .queryParam("reg_id",
                                        "11B00000")
                                .queryParam("tm_fc", "0")
                                .queryParam(
                                        "disp",
                                        "0"
                                )
                                .queryParam("help",0)
                                .queryParam(
                                        "authKey",
                                        apiConfig.getWeather()
                                                .getApikey()
                                )
                                .build()
                )
                .retrieve()
                .body(
                        String.class
                );
    }

}
