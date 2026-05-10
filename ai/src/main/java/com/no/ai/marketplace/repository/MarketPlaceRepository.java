package com.no.ai.marketplace.repository;

import com.no.ai.marketplace.domain.Marketplace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MarketPlaceRepository extends JpaRepository<Marketplace, Long> {
    Optional<Marketplace> findByName(String name);
}
