package com.no.ai.admin.controller;

import com.no.ai.global.exception.Message;
import com.no.ai.global.exception.StatusEnum;
import com.no.ai.global.service.FileService;
import com.no.ai.item.domain.Item;
import com.no.ai.item.dto.ItemDto;
import com.no.ai.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final ItemService itemService;
    private final FileService fileService;

    // 관리자가 등록하는 거임
    @PostMapping("/register")
    public ResponseEntity<Message> adminRegister(@RequestPart MultipartFile imageFile, @RequestPart ItemDto.CREATE dto) {

        Item item = itemService.registerByAdmin(dto);
        fileService.fileUpload(item, imageFile);

        Message message = new Message();

        message.setStatus(StatusEnum.OK);
        message.setMessage("성공 코드");

        return new ResponseEntity<>(message, HttpStatus.OK);
    }
}
