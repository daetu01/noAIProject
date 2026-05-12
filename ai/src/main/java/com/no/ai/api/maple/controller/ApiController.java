package com.no.ai.api.maple.controller;

import com.no.ai.api.maple.service.ApiService;
import com.no.ai.global.exception.Message;
import com.no.ai.global.exception.StatusEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/maple")
@RequiredArgsConstructor
public class ApiController {
    private final ApiService apiService;

    @GetMapping("/id")
    public ResponseEntity<Message> getData(@RequestParam String characterName) {
        String ocidCode = apiService.getMapleData(characterName);
        Message message = new Message();
        message.setMessage("성공 코드");
        message.setData(apiService.getBasicInfo(ocidCode));
        return new ResponseEntity<>(message, HttpStatus.OK);
    }
}
