package com.no.ai.api.city.repository;

import com.no.ai.api.city.domain.TrafficRisk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrafficRiskRepository extends JpaRepository<TrafficRisk, Long> {
    Optional<TrafficRisk> findBySpotNumAndYmdAndHour(
            String spotNum,
            LocalDate ymd,
            Integer hour
    );

    List<TrafficRisk> findByYmdAndHour(
            LocalDate ymd,
            Integer hour
    );

    Optional<TrafficRisk> findBySpotNumAndHour(String spotNum, int hour);

    Optional<TrafficRisk> findBySpotNum(String spotNum);


}
