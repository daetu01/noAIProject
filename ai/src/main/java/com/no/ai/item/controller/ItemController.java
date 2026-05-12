package com.no.ai.item.controller;

import com.no.ai.item.dto.ItemDto;
import com.no.ai.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/item")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    public List<ItemDto.GET> getItems() {
        return itemService.getItems();
    }

}
