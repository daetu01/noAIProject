package com.no.ai.api.maple.repository;

import com.no.ai.api.maple.domain.CharacterInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CharacterInfoRepository extends JpaRepository<CharacterInfo, Long> {
    Optional<CharacterInfo> findByCharacterName(String characterName);
}
