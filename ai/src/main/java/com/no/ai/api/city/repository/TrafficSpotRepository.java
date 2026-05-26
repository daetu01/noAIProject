package com.no.ai.api.city.repository;

import com.no.ai.api.city.domain.TrafficSpot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrafficSpotRepository extends JpaRepository<TrafficSpot, Long> {
    Optional<TrafficSpot> findBySpotNum(String spotNum);
}
