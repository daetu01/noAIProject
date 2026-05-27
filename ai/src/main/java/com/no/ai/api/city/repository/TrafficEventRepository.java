package com.no.ai.api.city.repository;

import com.no.ai.api.city.domain.TrafficEvent;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TrafficEventRepository extends JpaRepository<TrafficEvent, Long> {

    @Query("SELECT e FROM TrafficEvent e WHERE e.spotNum = :spotNum AND e.hour = :hour AND e.recordedAt >= :start AND e.recordedAt < :end ORDER BY e.recordedAt DESC LIMIT 1")
    Optional<TrafficEvent> findLatestBySpotAndHourAndDate(
            @Param("spotNum") String spotNum,
            @Param("hour") int hour,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
        SELECT e
        FROM TrafficEvent e
        WHERE e.recordedAt >= :time
    """)
    List<TrafficEvent> findRecentEvents(LocalDateTime time);
}
