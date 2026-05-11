package com.no.ai.inventory.controller;

import com.no.ai.global.exception.Message;
import com.no.ai.global.exception.StatusEnum;
import com.no.ai.global.security.details.CustomUserDetails;
import com.no.ai.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<Message> getMyInventory(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Message message = new Message();
        message.setStatus(StatusEnum.OK);
        message.setMessage("성공 코드");
        message.setData(inventoryService.getInventory(userDetails.getUser().getEmail()));
        return new ResponseEntity<>(message, HttpStatus.OK);
    }
}
