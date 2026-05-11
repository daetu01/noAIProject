package com.no.ai.trade.repository;

import com.no.ai.trade.domain.TradeHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeRepository extends JpaRepository<TradeHistory, Long> {
}
