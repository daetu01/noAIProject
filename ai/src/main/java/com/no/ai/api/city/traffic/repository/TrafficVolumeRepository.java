package com.no.ai.api.city.traffic.repository;

import com.no.ai.api.city.traffic.domain.TrafficVolume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrafficVolumeRepository extends JpaRepository<TrafficVolume, Long> {
    Optional<List<TrafficVolume>> findAllBySpotNumAndYmd(String spotNum, LocalDate ymd);
    Optional<TrafficVolume> findBySpotNumAndYmdAndHour(String spotNum, LocalDate ymd, int hour);
    Optional<List<TrafficVolume>> findByYmdAndHour(LocalDate ymd, int hour);
    @Query("""
    SELECT AVG(t.totalVolume)
    FROM TrafficVolume t 
    WHERE t.spotNum=:spotNum
""")
    Double findAverageVolume(String spotNum);
}


