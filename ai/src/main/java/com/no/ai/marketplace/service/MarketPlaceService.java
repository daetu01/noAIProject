package com.no.ai.marketplace.service;

import com.no.ai.global.security.details.CustomUserDetails;
import com.no.ai.item.domain.Item;
import com.no.ai.item.repository.ItemRepository;
import com.no.ai.marketplace.domain.Marketplace;
import com.no.ai.marketplace.dto.MarketPlaceDto;
import com.no.ai.marketplace.repository.MarketPlaceRepository;
import com.no.ai.user.domain.UserEntity;
import com.no.ai.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MarketPlaceService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final MarketPlaceRepository marketPlaceRepository;
    private final ModelMapper modelMapper;

    public void registerItem(CustomUserDetails user, MarketPlaceDto.CREATE dto) {
        // 시장에 등록 해야됨
        // 아이템 먼저 찾기
        Item item = itemRepository.findByName(dto.getName())
                .orElseThrow(() -> new IllegalArgumentException("찾을 수 없는 아이템입니다."));

        UserEntity userEntity = userRepository.findById(user.getUser().getUserId())
                .orElseThrow(() -> new IllegalArgumentException("없는 유저입니다."));

        Marketplace marketPlace = modelMapper.map(dto, Marketplace.class);

        marketPlace.updateItem(item);
        marketPlace.updateUser(userEntity);

        marketPlaceRepository.save(marketPlace);
    }

    public void updateItem(CustomUserDetails user, MarketPlaceDto.CREATE dto) {
        Marketplace marketplace = marketPlaceRepository.findByName(dto.getName())
                .orElseThrow(() -> new IllegalArgumentException("없는 거래 정보입니다."));

        UserEntity userEntity = userRepository.findById(user.getUser().getUserId())
                .orElseThrow(() -> new IllegalArgumentException("없는 유저입니다."));

        if (!marketplace.getRegisterUser().getNickName().equals(userEntity.getNickName())) {
            return;
        }

        marketplace.setPrice(dto.getPrice());
        marketPlaceRepository.save(marketplace);
    }
}
