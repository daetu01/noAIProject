package com.no.ai.inventory.service;

import com.no.ai.inventory.dto.InventoryDto;
import com.no.ai.user.domain.UserEntity;
import com.no.ai.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final UserRepository userRepository;

    public List<InventoryDto.GET> getInventory(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("없는 유저입니다."));

        if (user.getInventory() == null) {
            return Collections.emptyList();
        }

        return user.getInventory().getInventoryItems().stream()
                .map(item -> new InventoryDto.GET(
                        item.getItem().getId(),
                        item.getItem().getName(),
                        item.getQuantity(),
                        item.getItem().getDescription()
                ))
                .toList();
    }
}
