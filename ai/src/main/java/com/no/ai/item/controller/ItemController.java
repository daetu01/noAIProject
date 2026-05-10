package com.no.ai.item.controller;

import com.no.ai.global.service.FileService;
import com.no.ai.global.exception.Message;
import com.no.ai.global.exception.StatusEnum;
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
@RequestMapping("/item")
@RequiredArgsConstructor
public class ItemController {


}
