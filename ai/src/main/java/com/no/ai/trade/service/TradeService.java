package com.no.ai.trade.service;

import com.no.ai.item.domain.Item;
import com.no.ai.item.repository.ItemRepository;
import com.no.ai.marketplace.domain.Marketplace;
import com.no.ai.marketplace.dto.MarketPlaceDto;
import com.no.ai.marketplace.repository.MarketPlaceRepository;
import com.no.ai.trade.domain.TradeHistory;
import com.no.ai.trade.repository.TradeRepository;
import com.no.ai.user.domain.UserEntity;
import com.no.ai.user.repository.UserRepository;
import com.no.ai.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class TradeService {
    private final UserService userService;

    private final UserRepository userRepository;
    private final TradeRepository tradeRepository;
    private final MarketPlaceRepository marketPlaceRepository;
    private final ItemRepository itemRepository;

    public void buy(String email, MarketPlaceDto.CREATE item) {
        // 가진 금액보다 더 많이 든다면?
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("없는 유저입니다."));

        Marketplace marketplace = marketPlaceRepository.findById(item.getId())
                .orElseThrow(() -> new IllegalArgumentException("없는 거래 정보입니다."));

        // 갖고 있는 금액보다 가격이 비싸다면 ? 기각
        if (user.getMoney().getMoney() < marketplace.getPrice()) {
            // 에러 코드 발생
            return ;
        }

        // 차익을 저장
        user.getMoney().setMoney(user.getMoney().getMoney() - marketplace.getPrice());
        userRepository.save(user);

        // cellUser 탐색
        UserEntity cellUser = userRepository.findByNickName(marketplace.getRegisterUser().getNickName())
                .orElseThrow(() -> new IllegalArgumentException("없는 유저입니다."));

        cellUser.getMoney().setMoney(cellUser.getMoney().getMoney() + marketplace.getPrice());

        userRepository.save(cellUser);

        Item earnItem = itemRepository.findByName(item.getName())
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이템입니다."));

        userService.earnItem(email, earnItem);

        // tradeHistory build
        TradeHistory tradeHistory = TradeHistory.builder()
                .buyUser(user)
                .cellUser(cellUser)
                .build();

        // 거래 히스토리에 저장
        tradeRepository.save(tradeHistory);
    }
}
