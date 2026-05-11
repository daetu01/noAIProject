package com.no.ai.money.repository;

import com.no.ai.money.domain.Money;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MoneyRepository extends JpaRepository<Money, Long> {
}
