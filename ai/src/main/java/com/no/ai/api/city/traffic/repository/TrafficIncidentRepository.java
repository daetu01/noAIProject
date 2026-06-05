package com.no.ai.api.city.traffic.repository;

import com.no.ai.api.city.traffic.domain.TrafficIncident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TrafficIncidentRepository extends JpaRepository<TrafficIncident, Long> {
    Optional<TrafficIncident> findByAccId(String accId);

    @Query("""
    SELECT i
    FROM TrafficIncident i
    WHERE i.occurTime <= :endTime
    AND (i.expectedClearTime IS NULL OR i.expectedClearTime >= :startTime)
    AND i.accInfo LIKE %:keyword%
""")
    List<TrafficIncident> findMatchedIncidents(
            LocalDateTime startTime,
            LocalDateTime endTime,
            String keyword
    );
}
