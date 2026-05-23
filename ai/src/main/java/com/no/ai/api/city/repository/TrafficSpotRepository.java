package com.no.ai.api.city.repository;

import com.no.ai.api.city.domain.TrafficSpot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrafficSpotRepository extends JpaRepository<TrafficSpot, Long> {
}
