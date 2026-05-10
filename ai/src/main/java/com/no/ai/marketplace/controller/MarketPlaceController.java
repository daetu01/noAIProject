package com.no.ai.marketplace.controller;

import com.no.ai.global.exception.Message;
import com.no.ai.global.exception.StatusEnum;
import com.no.ai.global.security.details.CustomUserDetails;
import com.no.ai.item.dto.ItemDto;
import com.no.ai.item.service.ItemService;
import com.no.ai.marketplace.dto.MarketPlaceDto;
import com.no.ai.marketplace.service.MarketPlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/market")
public class MarketPlaceController {
    private final MarketPlaceService marketPlaceService;

    /**
     * 상품 등록 로직
     * @param userDetails
     * @param dto
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity<Message> registerItem(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody MarketPlaceDto.CREATE dto) {
        marketPlaceService.registerItem(userDetails, dto);

        Message message = new Message();

        message.setStatus(StatusEnum.OK);
        message.setMessage("성공 코드");
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    /**
     * 가격 업데이트 로직
     * @param userDetails
     * @param dto
     * @return
     */
    @PutMapping("/update")
    public ResponseEntity<Message> updateItem(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody MarketPlaceDto.CREATE dto) {
        marketPlaceService.updateItem(userDetails, dto);
        Message message = new Message();

        message.setStatus(StatusEnum.OK);
        message.setMessage("성공 코드");
        return new ResponseEntity<>(message, HttpStatus.OK);
    }
}
