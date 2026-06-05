package com.no.ai.api.city.weather.repository;

import com.no.ai.api.city.weather.domain.ForecastSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ForecastRepository extends JpaRepository<ForecastSnapshot, Long> {
}
