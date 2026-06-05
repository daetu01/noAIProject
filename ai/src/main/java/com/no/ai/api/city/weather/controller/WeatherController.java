package com.no.ai.api.city.weather.controller;

import com.no.ai.api.city.weather.dto.ForecastDto;
import com.no.ai.api.city.weather.dto.WeatherDto;
import com.no.ai.api.city.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/weather")
@RequiredArgsConstructor
@RestController
public class WeatherController {
    private final WeatherService weatherService;

    @GetMapping("/daily")
    public WeatherDto.WeatherRow getDailyWeather(@RequestParam String ymd) {
        return weatherService.dailyWeather(ymd);
    }

    @GetMapping("/daily/short")
    public List<ForecastDto.Response> getForecastWeather() {
        return weatherService.forecastWeather();
    }
}
