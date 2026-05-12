package com.no.ai.api.maple.repository;

import com.no.ai.api.maple.domain.CharacterInfo;
import com.no.ai.api.maple.domain.CharacterStat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CharacterStatRepository extends JpaRepository<CharacterStat, Long> {
    Optional<CharacterStat> findByCharacterInfo(CharacterInfo characterInfo);
}
