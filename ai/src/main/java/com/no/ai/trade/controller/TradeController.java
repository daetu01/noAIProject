package com.no.ai.trade.controller;

import com.no.ai.global.security.details.CustomUserDetails;
import com.no.ai.marketplace.dto.MarketPlaceDto;
import com.no.ai.trade.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trade")
@RequiredArgsConstructor
public class TradeController {
    private final TradeService tradeService;

    @PostMapping("/buy")
    public void buy(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody MarketPlaceDto.CREATE trade) {
        // 무슨 아이템인지
        tradeService.buy(userDetails.getUser().getEmail(),trade);
    }
}
