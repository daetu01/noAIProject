package com.no.ai.trade.service;

import com.no.ai.marketplace.dto.MarketPlaceDto;
import com.no.ai.user.domain.UserEntity;
import com.no.ai.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TradeService {
    private final UserRepository userRepository;

    public void buy(String email, MarketPlaceDto.CREATE item) {
        // 가진 금액보다 더 많이 든다면?
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("없는 유저입니다."));


        // 갖고 있는 금액보다 가격이 비싸다면 ? 기각
        if (user.getMoney().getMoney() < item.getPrice()) {
            // 에러 코드 발생
        }

        // 차익을 저장
        user.getMoney().setMoney(user.getMoney().getMoney() - item.getPrice());
        userRepository.save(user);

    }
}
