package com.no.ai.api.city.weather.repository;

import com.no.ai.api.city.weather.domain.WeatherSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface WeatherRepository extends JpaRepository<WeatherSnapshot, Long> {
    Optional<WeatherSnapshot> findTopByYmdAndStnIdOrderByIdDesc(LocalDate ymd, String stnId);
}
